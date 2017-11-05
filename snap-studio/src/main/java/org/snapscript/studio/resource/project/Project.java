package org.snapscript.studio.resource.project;

import static org.snapscript.studio.configuration.ProjectConfiguration.CLASSPATH_FILE;
import static org.snapscript.studio.configuration.ProjectConfiguration.PROJECT_FILE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import org.snapscript.agent.ClassPathUpdater;
import org.snapscript.common.store.NotFoundException;
import org.snapscript.common.store.Store;
import org.snapscript.studio.Workspace;
import org.snapscript.studio.configuration.ConfigurationClassLoader;
import org.snapscript.studio.configuration.ConfigurationReader;
import org.snapscript.studio.configuration.Dependency;
import org.snapscript.studio.configuration.ProjectConfiguration;

public class Project implements Store {
   
   private final ConfigurationClassLoader classLoader;
   private final ConfigurationReader reader;
   private final ProjectFileSystem fileSystem;
   private final Workspace workspace;
   private final String projectName;
   private final String projectDirectory;

   public Project(ConfigurationReader reader, Workspace workspace, String projectDirectory, String projectName) {
      this.classLoader = new ConfigurationClassLoader(this);
      this.fileSystem = new ProjectFileSystem(this);
      this.projectDirectory = projectDirectory;
      this.projectName = projectName;
      this.workspace = workspace;
      this.reader = reader;
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
   
   public ProjectLayout getLayout() {
      try {
         ProjectConfiguration configuration = reader.loadProjectConfiguration(projectName);
         return configuration.getProjectLayout();
      } catch (Exception e) {
         throw new IllegalStateException("Could not get source path for '" + projectName + "'", e);
      }
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
         throw new IllegalStateException("Could not determine dependencies for '" + projectName+ "'", e);
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
      List<File> dependencies = resolveDependencies();
      
      try {
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
         throw new IllegalStateException("Could not build class path", e);
      }
   }
   
   private List<File> resolveDependencies(){ 
      try {
         ProjectConfiguration configuration = reader.loadProjectConfiguration(projectName);
         List<Dependency> dependencies = configuration.getDependencies();
         
         if(!dependencies.isEmpty()) {
            return workspace.resolveDependencies(dependencies);
         }
         return Collections.emptyList();
      } catch(Exception e) {
         throw new IllegalStateException("Could not load dependencies for '" + projectName + "'", e);
      }
   }
}