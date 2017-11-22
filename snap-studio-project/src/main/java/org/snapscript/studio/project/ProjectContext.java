package org.snapscript.studio.project;

import static org.snapscript.studio.project.config.ProjectConfiguration.CLASSPATH_FILE;
import static org.snapscript.studio.project.config.ProjectConfiguration.PROJECT_FILE;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import org.snapscript.studio.agent.ClassPathUpdater;
import org.snapscript.studio.index.IndexDatabase;
import org.snapscript.studio.index.IndexScanner;
import org.snapscript.studio.project.config.ConfigurationReader;
import org.snapscript.studio.project.config.Dependency;
import org.snapscript.studio.project.config.ProjectConfiguration;

public class ProjectContext {
   
   private static final String INDEX_DATABASE = "database";
   
   private final ConfigurationReader reader;
   private final Workspace workspace;
   private final Project project;

   public ProjectContext(ConfigurationReader reader, Workspace workspace, Project project) {
      this.workspace = workspace;
      this.project = project;
      this.reader = reader;
   }

   public synchronized ProjectConfiguration getConfiguration() {
      String projectName = project.getProjectName();
      
      try {
         return reader.loadProjectConfiguration(projectName);
      } catch (Exception e) {
         workspace.getLogger().info("Could not read .project file for '" + projectName + "'", e);
      }
      return null;
   }
   
   public synchronized ProjectLayout getLayout() {
      String projectName = project.getProjectName();
      
      try {
         return getConfiguration().getProjectLayout();
      } catch (Exception e) {
         workspace.getLogger().info("Could not read .project file for '" + projectName + "'", e);
      }
      return new ProjectLayout();
   }
   
   public synchronized IndexDatabase getIndexDatabase(){
      ProjectConfiguration configuraton = getConfiguration();
      IndexDatabase database = configuraton.getAttribute(INDEX_DATABASE);
      
      if(database == null) {
         database = new IndexScanner(
            project.getClassLoader(),
            project.getProjectContext(), 
            workspace.getExecutor(), 
            project.getSourcePath(), 
            project.getProjectName(), 
            getLayout().getPrefixes());

         configuraton.setAttribute(INDEX_DATABASE, database);
      }
      return database;
   }

   public synchronized List<File> getDependencies() {
      try {
         String classPath = getClassPath();
         List<File> files = ClassPathUpdater.parseClassPath(classPath);
         
         for(File file : files) {
            if(!file.exists()) {
               return getDeclaredDependencies(); // force a maven lookup
            }
         }
         return files;
      } catch(Exception e) {
         throw new IllegalStateException(e.getMessage(), e);
      }
   }
   
   public synchronized String getClassPath() {
      File projectFile = project.getFileSystem().getFile(PROJECT_FILE);
      File classPathFile = project.getFileSystem().getFile(CLASSPATH_FILE);
      
      try {
         if(!classPathFile.exists()) {
            FileWriter writer = new FileWriter(classPathFile);
            
            try {
               String classPath = getClassPathFile();
               writer.write(classPath);
               return classPath;
            } finally {
               writer.close();
            }
         }
         if(projectFile.exists()) {
            long projectFileChange = projectFile.lastModified();
            long classPathFileChange = classPathFile.lastModified();
            
            if(projectFileChange > classPathFileChange) {
               FileWriter writer = new FileWriter(classPathFile);
               
               try {
                  String classPath = getClassPathFile();  
                  writer.write(classPath);
                  return classPath;
               } finally {
                  writer.close();
               }
            }
         }
         return project.getFileSystem().readAsString(CLASSPATH_FILE);
      } catch(Exception e) {
         throw new IllegalStateException("Could not create " + CLASSPATH_FILE, e);
      }
   }
   
   private synchronized String getClassPathFile() throws Exception {
      String projectName = project.getProjectName();
      
      try {
         List<File> dependencies = getDeclaredDependencies();
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
   
   private synchronized List<File> getDeclaredDependencies(){ 
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
}