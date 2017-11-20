package org.snapscript.studio.project;

import static org.snapscript.studio.project.config.ProjectConfiguration.CLASSPATH_FILE;
import static org.snapscript.studio.project.config.ProjectConfiguration.PROJECT_FILE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import org.snapscript.common.store.NotFoundException;
import org.snapscript.common.store.Store;
import org.snapscript.compile.StoreContext;
import org.snapscript.core.Context;
import org.snapscript.studio.agent.ClassPathUpdater;
import org.snapscript.studio.common.DirectoryWatcher;
import org.snapscript.studio.common.FileDirectory;
import org.snapscript.studio.index.IndexDatabase;
import org.snapscript.studio.index.IndexScanner;
import org.snapscript.studio.project.config.ClassPathExecutor;
import org.snapscript.studio.project.config.ConfigurationClassLoader;
import org.snapscript.studio.project.config.ConfigurationReader;
import org.snapscript.studio.project.config.Dependency;
import org.snapscript.studio.project.config.ProjectConfiguration;

public class Project implements FileDirectory {
   
   private static final String INDEX_DATABASE = "database";
   
   private final ConfigurationClassLoader classLoader;
   private final ConfigurationReader reader;
   private final ProjectFileSystem fileSystem;
   private final Workspace workspace;
   private final String projectName;
   private final String projectDirectory;
   private final Store store;

   public Project(ConfigurationReader reader, Workspace workspace, String projectDirectory, String projectName) {
      this.classLoader = new ConfigurationClassLoader(this);
      this.fileSystem = new ProjectFileSystem(this);
      this.store = new ProjectStore();
      this.projectDirectory = projectDirectory;
      this.projectName = projectName;
      this.workspace = workspace;
      this.reader = reader;
   }
   
   public Workspace getWorkspace(){
      return workspace;
   }
   
   public String getProjectDirectory() {
      return projectDirectory;
   }

   public String getProjectName() {
      return projectName;
   }

   public ProjectFileSystem getFileSystem() {
      return fileSystem;
   }
   
   public ClassLoader getClassLoader() {
      return classLoader.getClassLoader();
   }
   
   public long getModificationTime(){
      return DirectoryWatcher.lastModified(getSourcePath());
   }

   public File getSourcePath() {
      try {
         return workspace.createFile(projectName);
      } catch (Exception e) {
         throw new IllegalStateException("Could not get source path for '" + projectName + "'", e);
      }
   }

   public File getProjectPath() {
      try {
         return workspace.createFile(projectName);
      } catch (Exception e) {
         throw new IllegalStateException("Could not get project path for '" + projectName + "'", e);
      }
   }
   
   public Context getProjectContext() {
      Executor threadPool = workspace.getExecutor();
      try {
         ClassPathExecutor executor = new ClassPathExecutor(this, threadPool);
         return new StoreContext(store, executor);
      }catch(Exception e) {
         throw new IllegalStateException("Could not create context for '" + projectName + "'", e);
      }
   }
   
   public IndexDatabase getIndexDatabase(){
      ProjectConfiguration configuraton = getConfiguration();
      IndexDatabase database = configuraton.getAttribute(INDEX_DATABASE);
      
      if(database == null) {
         database = new IndexScanner(
            getClassLoader(),
            getProjectContext(), 
            getWorkspace().getExecutor(), 
            getSourcePath(), 
            getProjectName(), 
            getLayout().getPrefixes());

         configuraton.setAttribute(INDEX_DATABASE, database);
      }
      return database;
   }
   
   public boolean isLayoutPath(String resource) {
      return getLayout().isLayoutPath(resource);
   }
   
   public String getRealPath(String resource) {
      return getLayout().getRealPath(getSourcePath(), resource);
   }
   
   public String getScriptPath(String resource) {
      return getLayout().getDownloadPath(getSourcePath(), resource);
   }
   
   public ProjectConfiguration getConfiguration() {
      try {
         return reader.loadProjectConfiguration(projectName);
      } catch (Exception e) {
         workspace.getLogger().info("Could not read .project file for '" + projectName + "'", e);
      }
      return null;
   }
   
   public ProjectLayout getLayout() {
      try {
         return getConfiguration().getProjectLayout();
      } catch (Exception e) {
         workspace.getLogger().info("Could not read .project file for '" + projectName + "'", e);
      }
      return new ProjectLayout();
   }

   public List<File> getDependencies() {
      try {
         String classPath = getClassPath();
         List<File> files = ClassPathUpdater.parseClassPath(classPath);
         
         for(File file : files) {
            if(!file.exists()) {
               return resolveDependencies(); // force a maven lookup
            }
         }
         return files;
      } catch(Exception e) {
         throw new IllegalStateException(e.getMessage(), e);
      }
   }
   
   public String getClassPath() {
      File projectFile = fileSystem.getFile(PROJECT_FILE);
      File classPathFile = fileSystem.getFile(CLASSPATH_FILE);
      
      try {
         if(!classPathFile.exists()) {
            FileWriter writer = new FileWriter(classPathFile);
            String classPath = createClassPath();
            
            writer.write(classPath);
            writer.close();
            
            return classPath;
         }
         if(projectFile.exists()) {
            long projectFileChange = projectFile.lastModified();
            long classPathFileChange = classPathFile.lastModified();
            
            if(projectFileChange > classPathFileChange) {
               FileWriter writer = new FileWriter(classPathFile);
               String classPath = createClassPath();
               
               writer.write(classPath);
               writer.close();
               
               return classPath;
            }
         }
         return fileSystem.readAsString(CLASSPATH_FILE);
      } catch(Exception e) {
         throw new IllegalStateException("Could not create " + CLASSPATH_FILE, e);
      }
   }
   
   private String createClassPath() throws Exception {
      try {
         List<File> dependencies = resolveDependencies();
         StringBuilder builder = new StringBuilder();
   
         builder.append(".");
         builder.append("\n");
         
         if(dependencies != null) {
            for(File dependency : dependencies) {
               if(!dependency.exists()) {
                  throw new IllegalStateException("Could not find dependency " + dependency);
               }
               String normal = dependency.getCanonicalPath();
               
               builder.append(normal);
               builder.append("\n");
            }
         }
         return builder.toString();
      } catch(Exception e) {
         StringWriter writer = new StringWriter();
         PrintWriter printer = new PrintWriter(writer);
         
         e.printStackTrace(printer);
         printer.close();
         workspace.getLogger().info("Could not create class path for project '" + projectName+ "': " + writer);
         
         return writer.toString();
      }
   }
   
   private List<File> resolveDependencies(){ 
      try {
         ProjectConfiguration configuration = getConfiguration();
         List<Dependency> dependencies = configuration.getDependencies();
         
         if(!dependencies.isEmpty()) {
            return workspace.resolveDependencies(dependencies);
         }
         return Collections.emptyList();
      } catch(Exception e) {
         throw new IllegalStateException(e.getMessage(), e);
      }
   }
   
   
   private class ProjectStore implements Store {
   
      @Override
      public InputStream getInputStream(String path) {
         try {
            ProjectLayout layout = getLayout();
            File rootPath = getSourcePath();
            String projectPath = layout.getRealPath(rootPath, path);
            File realFile = fileSystem.getFile(projectPath);
            return new FileInputStream(realFile);
         } catch(Exception e) {
            throw new NotFoundException("Could not get source path for '" + path + "'", e);
         }
      }
   
      @Override
      public OutputStream getOutputStream(String path) {
         return System.out;
      }
   }
}