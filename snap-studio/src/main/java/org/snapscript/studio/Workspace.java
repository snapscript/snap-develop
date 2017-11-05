package org.snapscript.studio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.simpleframework.http.Path;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.studio.configuration.OperatingSystem;
import org.snapscript.studio.configuration.ConfigurationReader;
import org.snapscript.studio.configuration.Dependency;
import org.snapscript.studio.configuration.ProjectConfiguration;
import org.snapscript.studio.resource.project.Project;
import org.snapscript.studio.resource.project.ProjectManager;

public class Workspace {

   private final ConfigurationReader reader;
   private final ProjectManager manager;
   private final ProcessLogger logger;
   private final File root;

   public Workspace(ProcessLogger logger, File root, String mode) {
      this.reader = new ConfigurationReader(this);
      this.manager = new ProjectManager(reader, this, mode);
      this.logger = logger;
      this.root = root;
   }
   
   public File getRoot() {
      return createWorkspace();
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
      try {
         return reader.loadWorkspaceConfiguration().getDependencies(dependencies);
      } catch(Exception e) {
         throw new IllegalStateException("Could not resolve dependencies", e);
      }
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
         
         if(!directory.exists()){
            if(!directory.mkdirs()) {
               throw new IllegalStateException("Could not build work directory " + directory);
            }
            File ignore = new File(directory, ".gitignore");
            OutputStream stream = new FileOutputStream(ignore);
            PrintStream print = new PrintStream(stream);
            print.println("/.temp/");
            print.close();
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
}