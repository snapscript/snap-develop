package org.snapscript.develop.http.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
      response.setStatus(Status.OK);

      try {
         if(handleFindBackup(request, response)) {
            handleNotFound(request, response);
         } else {
            handleBackupHistory(request, response);
         }
      }catch(Exception e) {
         handleNotFound(request, response);
      }
   }
   
   private void handleBackupHistory(Request request, Response response) throws Throwable {
      Path path = request.getPath(); 
      Project project = builder.createProject(path);
      ProjectFileSystem system = project.getFileSystem();
      File file = system.getFile(path);
      String name = project.getProjectName();
      List<BackupFile> files = manager.findAllBackups(file, name);
      PrintStream stream = response.getPrintStream();
      String text = gson.toJson(files);
      
      response.setContentType("application/json");
      stream.println(text);
      stream.close();
   }
   
   private boolean handleFindBackup(Request request, Response response) throws Throwable {
      String timeStamp = request.getParameter("time"); // do we load file
      
      if(timeStamp != null) {
         Path path = request.getPath(); 
         Project project = builder.createProject(path);
         ProjectFileSystem system = project.getFileSystem();
         File file = system.getFile(path);
         String name = project.getProjectName();
         List<BackupFile> files = manager.findAllBackups(file, name);
         
         for(BackupFile entry : files) {
            if(entry.getTimeStamp().equals(timeStamp)) {
               response.setContentType("text/plain");
               File backupFile = entry.getFile();
               OutputStream output = response.getOutputStream();
               InputStream source = new FileInputStream(backupFile);
               byte[] chunk = new byte[1024];
               int count = 0;
               
               while((count = source.read(chunk)) != -1){
                  output.write(chunk, 0, count);
               }
               source.close();
               output.close();
               return true; // we found it
            }
         }
      }
      return false;
   }
   
   private void handleNotFound(Request request, Response response) throws Throwable {
      Path path = request.getPath(); 
      String projectPath = path.getPath(2);
      PrintStream out = response.getPrintStream();
      response.setStatus(Status.NOT_FOUND);
      response.setContentType("text/plain");
      
      if(projectPath.endsWith(Reserved.SCRIPT_EXTENSION)){
         out.println("// No source found for " + projectPath);
      }
      out.close();
   }
}