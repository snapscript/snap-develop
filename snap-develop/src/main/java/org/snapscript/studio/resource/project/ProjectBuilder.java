package org.snapscript.studio.resource.project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.simpleframework.http.Path;
import org.snapscript.studio.Workspace;

public class ProjectBuilder {
   
   private static final String DEFAULT_PROJECT = "default";

   private final Map<String, Project> projects;
   private final Workspace workspace;
   private final ProjectMode mode;
   private final Project single;
   
   public ProjectBuilder(Workspace workspace, String mode){
      this.projects = new ConcurrentHashMap<String, Project>();
      this.single = new Project(workspace, ".", DEFAULT_PROJECT);
      this.mode = new ProjectMode(mode);
      this.workspace = workspace;
   }
   
   public File getRoot() {
      return workspace.create();
   }
   
   public Project getProject(String name){ 
      return projects.get(name);
   }
   
   public Project getProject(Path path){ // /project/<project-name>/ || /project/default
      if(mode.isMultipleMode()) { // multiple project support
         String projectPrefix = path.getPath(1, 1); // /<project-name>
         String projectName = projectPrefix.substring(1); // <project-name>
         
         return projects.get(projectName);
      }
      return single;
   }
   
   public Project createProject(Path path){ // /project/<project-name>/ || /project/default
      if(mode.isMultipleMode()) { // multiple project support
         String projectPrefix = path.getPath(1, 1); // /<project-name>
         String projectName = projectPrefix.substring(1); // <project-name>
         Project project = projects.get(projectName);
         
         if(project == null) {
            project = new Project(workspace, projectName, projectName);
            projects.put(projectName, project);
         }
         File file = project.getProjectPath();
         
         if(!file.exists()) {
            file.mkdirs();
            createDefaultProject(file);
         }
         return project;
      }
      return single;
   }
   
   private void createDefaultProject(File file) {
      try {
         File directory = file.getCanonicalFile();
         
         if(!directory.exists() && !directory.mkdirs()) {
            throw new IllegalStateException("Could not build project directory " + directory);
         }
         File ignore = new File(directory, ".gitignore");
         OutputStream stream = new FileOutputStream(ignore);
         PrintStream print = new PrintStream(stream);
         print.println("/temp/");
         print.println("/.temp/");   
         print.println("/.backup/");     
         print.close();
      }catch(Exception e) {
         e.printStackTrace();
      }
   }
}