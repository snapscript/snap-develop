package org.snapscript.studio.project;

import static org.snapscript.studio.project.config.ProjectConfiguration.CLASSPATH_FILE;
import static org.snapscript.studio.project.config.ProjectConfiguration.INDEX_FILE;
import static org.snapscript.studio.project.config.ProjectConfiguration.PROJECT_FILE;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.snapscript.studio.agent.ClassPathUpdater;
import org.snapscript.studio.index.IndexDatabase;
import org.snapscript.studio.index.IndexScanner;
import org.snapscript.studio.index.classpath.ClassFile;
import org.snapscript.studio.index.classpath.IndexPath;
import org.snapscript.studio.index.classpath.ProjectClassPath;
import org.snapscript.studio.index.classpath.ResourcePathClassFile;
import org.snapscript.studio.project.config.ConfigurationReader;
import org.snapscript.studio.project.config.Dependency;
import org.snapscript.studio.project.config.DependencyFile;
import org.snapscript.studio.project.config.ProjectConfiguration;

@Slf4j
public class ProjectContext {
   
   private static final String INDEX_DATABASE_KEY = "database";
   private static final String CLASSPATH_KEY = "classpath";
   private static final String INDEX_KEY = "index";
   
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
      IndexDatabase database = configuraton.getAttribute(INDEX_DATABASE_KEY);
      
      if(database == null) {
         database = new IndexScanner(
            getIndexFile(),
            project.getProjectContext(), 
            workspace.getExecutor(), 
            project.getSourcePath(), 
            project.getProjectName(), 
            getLayout().getPrefixes());

         configuraton.setAttribute(INDEX_DATABASE_KEY, database);
      }
      return database;
   }

   public synchronized List<DependencyFile> getDependencies() {
      List<DependencyFile> dependencies = new ArrayList<DependencyFile>();
      
      try {
         ClassPathFile classPath = getClassPath();
         String content = classPath.getPath();
         List<File> files = ClassPathUpdater.parseClassPath(content);
         List<String> errors = classPath.getErrors();
         
         for(File file : files) {
            if(!file.exists()) {
               return getDeclaredDependencies(); // force a maven lookup
            }
            DependencyFile entry = new DependencyFile(file);
            dependencies.add(entry);
         }
         for(String error : errors) {
            DependencyFile entry = new DependencyFile(null, error);
            dependencies.add(entry);
         }
      } catch(Exception e) {
         throw new IllegalStateException(e.getMessage(), e);
      }
      return dependencies;
   }
   
   public synchronized ClassPathFile getClassPath() {
      File projectFile = getProjectFile(PROJECT_FILE);
      File classPathFile = getProjectFile(CLASSPATH_FILE);
      ProjectConfiguration configuraton = getConfiguration();
      
      try {
         if(!classPathFile.exists()) {
            FileWriter writer = new FileWriter(classPathFile);
            
            try {
               ClassPathFile classPath = getClassPathFile();
               String text = classPath.getPath();
               configuraton.setAttribute(CLASSPATH_KEY, classPath);
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
                  configuraton.setAttribute(CLASSPATH_KEY, classPath);
                  writer.write(text);
                  return classPath;
               } finally {
                  writer.close();
               }
            }
         }
         ClassPathFile classPath = configuraton.getAttribute(CLASSPATH_KEY);
         
         if(classPath == null) {
            classPath = getClassPathFile(CLASSPATH_FILE);
            configuraton.setAttribute(CLASSPATH_KEY, classPath);
         }
         return classPath;
      } catch(Exception e) {
         configuraton.setAttribute(CLASSPATH_KEY, null);
         throw new IllegalStateException("Could not create " + CLASSPATH_FILE, e);
      }
   }
   
   public synchronized IndexPath getIndexFile() {
      File projectFile = getProjectFile(PROJECT_FILE);
      File indexFile = getProjectFile(INDEX_FILE);
      ProjectConfiguration configuraton = getConfiguration();
      
      try {
         if(!indexFile.exists()) {
            FileWriter writer = new FileWriter(indexFile);
            
            try {
               IndexPath indexPath = getNewIndexPath();
               String text = indexPath.getText();
               configuraton.setAttribute(INDEX_KEY, indexPath);
               writer.write(text);
               return indexPath;
            } finally {
               writer.close();
            }
         }
         if(projectFile.exists()) {
            long projectFileChange = projectFile.lastModified();
            long classPathFileChange = indexFile.lastModified();
            
            if(projectFileChange > classPathFileChange) {
               IndexPath indexPath = getIndexFile(INDEX_FILE);
               FileWriter writer = new FileWriter(indexFile);
               
               try {
                  String text = indexPath.getText();
                  configuraton.setAttribute(INDEX_KEY, indexPath);
                  writer.write(text);
                  return indexPath;
               } finally {
                  writer.close();
               }
            }
         }
         IndexPath indexPath = configuraton.getAttribute(INDEX_KEY);
         
         if(indexPath == null) {
            indexPath = getIndexFile(INDEX_FILE);
            configuraton.setAttribute(INDEX_KEY, indexPath);
         }
         return indexPath;
      } catch(Exception e) {
         configuraton.setAttribute(INDEX_KEY, null);
         throw new IllegalStateException("Could not create " + INDEX_FILE, e);
      }
   }
   
   private synchronized IndexPath getIndexFile(String name) {
      StringBuilder builder = new StringBuilder();
      List<ClassFile> files = new ArrayList<ClassFile>();
      ClassLoader loader = project.getClassLoader();
      
      try {
         String content = project.getFileSystem().readAsString(name);
         String[] lines = content.split("\\r?\\n");
         
         for(String line : lines) {
            String trimmed = line.trim();
            
            if(!line.isEmpty()) {
               if(!line.startsWith("#")) {
                  ClassFile file = new ResourcePathClassFile(trimmed, loader);
                  files.add(file);
               } 
               builder.append(trimmed); // keep comments also
               builder.append("\n");
            }
         }
      } catch(Exception e) {
         return getNewIndexPath();
      }
      String text = builder.toString();
      return new IndexPath(loader, files, text);
   }
   
   private synchronized IndexPath getNewIndexPath() {
      Date date = new Date();
      StringBuilder builder = new StringBuilder();
      List<ClassFile> files = new ArrayList<ClassFile>();
      ClassLoader loader = project.getClassLoader();
      
      try {
         files = ProjectClassPath.getClassFiles(loader);
         
         builder.append("# ");
         builder.append(date);
         builder.append("\n");
         
         for(ClassFile file : files) {
            String path = file.getResourceName();
         
            builder.append(path);
            builder.append("\n");
         }
      } catch(Exception e) {
         return null;
      }
      String text = builder.toString();
      return new IndexPath(loader, files, text);
   }
   
   private synchronized ClassPathFile getClassPathFile(String name) {
      StringBuilder builder = new StringBuilder();
      List<String> errors = new ArrayList<String>();
      
      try {
         String content = project.getFileSystem().readAsString(name);
         String[] lines = content.split("\\r?\\n");
         
         for(String line : lines) {
            String trimmed = line.trim();
            
            if(!line.isEmpty()) {
               if(line.startsWith("#!")) {
                  String message = line.substring(2);
                  String error = message.trim();
                  
                  errors.add(error);
               } else if(!line.startsWith("#")) {
                  builder.append(trimmed);
                  builder.append("\n");
               }
            }
         }
      } catch(Exception e) {
         return null;
      }
      String path = builder.toString();
      return new ClassPathFile(path, errors);
   }
   
   
   private synchronized File getProjectFile(String name){
      return project.getFileSystem().getFile(name);
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
                  builder.append("#! ");
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