package org.snapscript.studio;

import static org.snapscript.studio.configuration.WorkspaceConfiguration.WORKSPACE_FILE;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import org.simpleframework.http.Path;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.common.thread.ThreadPool;
import org.snapscript.studio.configuration.ConfigurationReader;
import org.snapscript.studio.configuration.Dependency;
import org.snapscript.studio.configuration.ProjectConfiguration;
import org.snapscript.studio.resource.project.Project;
import org.snapscript.studio.resource.project.ProjectManager;

public class Workspace {

   private final ConfigurationReader reader;
   private final ProjectManager manager;
   private final ProcessLogger logger;
   private final ThreadPool pool;
   private final File root;

   public Workspace(ProcessLogger logger, File root, String mode) {
      this.reader = new ConfigurationReader(this);
      this.pool = new ThreadPool(6);
      this.manager = new ProjectManager(reader, this, mode);
      this.logger = logger;
      this.root = root;
   }
   
   public File getRoot() {
      return createWorkspace();
   }
   
   public Executor getExecutor(){
      return pool;
   }
   
   public ProcessLogger getLogger() {
      return logger;
   }
   
   public Project getProject(String name){ 
      return manager.getProject(name);
   }
   
   public Project getProject(Path path){ // /project/<project-name>/ || /project/default
      return manager.getProject(path);
   }
   
   public Project createProject(String name){ 
      return manager.createProject(name);
   }
   
   public Project createProject(Path path){ // /project/<project-name>/ || /project/default
      return manager.createProject(path);
   }
   
   public Map<String, String> getEnvironmentVariables() {
      try {
         return reader.loadWorkspaceConfiguration().getEnvironmentVariables();
      } catch(Exception e) {
         throw new IllegalStateException("Could not resolve environment variables", e);
      }  
   }
   
   public List<String> getArguments() {
      try {
         return reader.loadWorkspaceConfiguration().getArguments();
      } catch(Exception e) {
         throw new IllegalStateException("Could not resolve arguments", e);
      }  
   }
   
   public List<File> resolveDependencies(List<Dependency> dependencies) {
      return reader.loadWorkspaceConfiguration().getDependencies(dependencies);
   }
   
   public File createFile(String name) {
      File file = new File(root, name);
      
      try {
         File directory = file.getParentFile();
         
         if(!directory.exists()) {
            directory.mkdirs();
         }
         return file.getCanonicalFile();
      }catch(Exception e) {
         throw new IllegalStateException("Could not create directory " + file, e);
      }
   }
   
   public File createWorkspace() {
      try {
         File directory = root.getCanonicalFile();
         File workspaceFile = new File(directory, WORKSPACE_FILE);
         
         if(!directory.exists() || !workspaceFile.exists()){
            directory.mkdirs();
            createDefaultWorkspace(directory);
         }
         getProjects();// resolve the dependencies
         return directory;
      }catch(Exception e) {
         throw new IllegalStateException("Could not create directory " + root, e);
      }
   }
   
   public List<Project> getProjects() {
      List<Project> projects = new ArrayList<Project>();
      
      try {
         File workspace = root.getCanonicalFile();
         
         if(workspace.exists()) {
            File[] directories = workspace.listFiles();
            
            if(projects != null) {
               for(File directory : directories) {
                  String name = directory.getName();
                 
                  if(directory.isDirectory() && !name.startsWith(".")) {
                     File file = new File(directory, ProjectConfiguration.PROJECT_FILE);
                     
                     if(file.exists()) {
                        Project project = createProject(name);
                        project.getClassPath(); // resolve dependencies
                     }
                  }
               }
            }
         }
      }catch(Exception e) {
         throw new IllegalStateException("Could not get projects in directory " + root, e);
      }
      return projects;
   }
   
   private void createDefaultWorkspace(File file) {
      try {
         File directory = file.getCanonicalFile();
         
         if(!directory.exists() && !directory.mkdirs()) {
            throw new IllegalStateException("Could not build project directory " + directory);
         }
         createDefaultFile(directory, ".gitignore", "/.display\n/.workspace\n/.temp/\n/.backup/\n");
         createDefaultFile(directory, ".workspace", "<workspace></workspace>\n");
      }catch(Exception e) {
         getLogger().info("Could not create default workspace at '" + file + "'", e);
      }
   }
   
   public static void createDefaultFile(File file, String name, String content) throws Exception {
      File directory = file.getCanonicalFile();
      
      if(!directory.exists() && !directory.mkdirs()) {
         throw new IllegalStateException("Could not build project directory " + directory);
      }
      File ignore = new File(directory, name);
      FileWriter stream = new FileWriter(ignore);
      
      stream.write(content);
      stream.close();
   }
}