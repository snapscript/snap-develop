package org.snapscript.develop.http.project;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.snapscript.core.Reserved;
import org.snapscript.develop.BackupFile;
import org.snapscript.develop.BackupManager;
import org.snapscript.develop.http.resource.Resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ProjectHistoryResource implements Resource {

   private final ProjectBuilder builder;
   private final BackupManager manager;
   private final Gson gson;
   
   public ProjectHistoryResource(ProjectBuilder builder, BackupManager manager){
      this.gson = new GsonBuilder().setPrettyPrinting().create();
      this.builder = builder;
      this.manager = manager;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      Path path = request.getPath(); 
      String projectPath = path.getPath(2);
      Project project = builder.createProject(path);
      ProjectFileSystem system = project.getFileSystem();
      File file = system.getFile(path);
      String name = project.getProjectName();
      PrintStream stream = response.getPrintStream();
      
      response.setStatus(Status.OK);
      response.setContentType("application/json");

      try {
         List<BackupFile> files = manager.findAllBackups(file, name);
         String text = gson.toJson(files);
         stream.println(text);
         stream.close();
      }catch(Exception e) {
         PrintStream out = response.getPrintStream();
         response.setStatus(Status.NOT_FOUND);
         response.setContentType("text/plain");
         
         if(projectPath.endsWith(Reserved.SCRIPT_EXTENSION)){
            out.println("// No source found for " + projectPath);
         }
         out.close();
      }
   }
}
