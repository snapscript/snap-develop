package org.snapscript.studio.project;

import static org.snapscript.studio.project.config.ProjectConfiguration.CLASSPATH_FILE;
import static org.snapscript.studio.project.config.ProjectConfiguration.PROJECT_FILE;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.snapscript.studio.agent.ClassPathUpdater;
import org.snapscript.studio.index.IndexDatabase;
import org.snapscript.studio.index.IndexScanner;
import org.snapscript.studio.project.config.ConfigurationReader;
import org.snapscript.studio.project.config.Dependency;
import org.snapscript.studio.project.config.DependencyFile;
import org.snapscript.studio.project.config.ProjectConfiguration;

@Slf4j
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
         log.info("Could not read .project file for '" + projectName + "'", e);
      }
      return null;
   }
   
   public synchronized ProjectLayout getLayout() {
      String projectName = project.getProjectName();
      
      try {
         return getConfiguration().getProjectLayout();
      } catch (Exception e) {
         log.info("Could not read .project file for '" + projectName + "'", e);
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

   public synchronized List<DependencyFile> getDependencies() {
      List<DependencyFile> dependencies = new ArrayList<DependencyFile>();
      
      try {
         ClassPathFile classPath = getClassPath();
         String content = classPath.getPath();
         List<File> files = ClassPathUpdater.parseClassPath(content);
         
         for(File file : files) {
            if(!file.exists()) {
               return getDeclaredDependencies(); // force a maven lookup
            }
            DependencyFile entry = new DependencyFile(file);
            dependencies.add(entry);
         }
      } catch(Exception e) {
         throw new IllegalStateException(e.getMessage(), e);
      }
      return dependencies;
   }
   
   public synchronized ClassPathFile getClassPath() {
      File projectFile = project.getFileSystem().getFile(PROJECT_FILE);
      File classPathFile = project.getFileSystem().getFile(CLASSPATH_FILE);
      
      try {
         if(!classPathFile.exists()) {
            FileWriter writer = new FileWriter(classPathFile);
            
            try {
               ClassPathFile classPath = getClassPathFile();
               String text = classPath.getPath();
               writer.write(text);
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
                  ClassPathFile classPath = getClassPathFile();
                  String text = classPath.getPath();
                  writer.write(text);
                  return classPath;
               } finally {
                  writer.close();
               }
            }
         }
         String classPath = project.getFileSystem().readAsString(CLASSPATH_FILE);
         return new ClassPathFile(classPath);
      } catch(Exception e) {
         throw new IllegalStateException("Could not create " + CLASSPATH_FILE, e);
      }
   }
   
   private synchronized ClassPathFile getClassPathFile() throws Exception {
      StringBuilder builder = new StringBuilder();
      List<String> errors = new ArrayList<String>();
      
      try {
         List<DependencyFile> dependencies = getDeclaredDependencies();
   
         builder.append(".");
         builder.append("\n");
         
         if(dependencies != null) {
            for(DependencyFile dependency : dependencies) {
               File file = dependency.getFile();
               String message = dependency.getMessage();
               
               if(message == null && file != null) {
                  String normal = file.getCanonicalPath();
                  builder.append(normal);
               } else if(message != null){
                  errors.add(message);
                  builder.append("# ");
                  builder.append(message);
               }
               builder.append("\n");
            }
         }
      } catch(Exception cause) {
         log.info("Could not create class path", cause);
      }
      String path = builder.toString();
      return new ClassPathFile(path, errors);
   }
   
   private synchronized List<DependencyFile> getDeclaredDependencies(){ 
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