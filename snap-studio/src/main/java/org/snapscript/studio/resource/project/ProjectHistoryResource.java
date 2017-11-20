package org.snapscript.studio.resource.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.snapscript.core.Reserved;
import org.snapscript.studio.common.resource.Resource;
import org.snapscript.studio.common.resource.ResourcePath;
import org.snapscript.studio.core.BackupFile;
import org.snapscript.studio.core.BackupManager;
import org.snapscript.studio.project.Project;
import org.snapscript.studio.project.ProjectFileSystem;
import org.snapscript.studio.project.Workspace;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component
@ResourcePath("/history.*")
public class ProjectHistoryResource implements Resource {

   private final BackupManager manager;
   private final Workspace workspace;
   private final Gson gson;
   
   public ProjectHistoryResource(Workspace workspace, BackupManager manager){
      this.gson = new GsonBuilder().setPrettyPrinting().create();
      this.workspace = workspace;
      this.manager = manager;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      response.setStatus(Status.OK);

      try {
         if(!handleFindBackup(request, response)) {
            handleBackupHistory(request, response);
         }
      }catch(Exception e) {
         handleNotFound(request, response);
      }
   }
   
   private void handleBackupHistory(Request request, Response response) throws Throwable {
      Path path = request.getPath(); 
      String projectPath = path.getPath(2);
      Project project = workspace.createProject(path);
      ProjectFileSystem system = project.getFileSystem();
      File file = system.getFile(path);
      String name = project.getProjectName();
      long modificationTime = file.lastModified();
      List<BackupFile> files = manager.findAllBackups(file, name);
      Date modificationDate = new Date(modificationTime);
      BackupFile currentFile = new BackupFile(file, projectPath, modificationDate, "current", name);
      
      files.add(0, currentFile);
      PrintStream stream = response.getPrintStream();
      String text = gson.toJson(files);
      
      response.setContentType("application/json");
      stream.println(text);
      stream.close();
   }
   
   private boolean handleFindBackup(Request request, Response response) throws Throwable {
      String timeStamp = request.getParameter("time"); // do we load file
      
      if(timeStamp != null) {
         response.setContentType("text/plain");
         InputStream source = findBackupFile(request, response);
         OutputStream output = response.getOutputStream();
         
         byte[] chunk = new byte[1024];
         int count = 0;
         
         while((count = source.read(chunk)) != -1){
            output.write(chunk, 0, count);
         }
         source.close();
         output.close();
         return true; // we found it
      }
      return false;
   }
   
   private InputStream findBackupFile(Request request, Response response) throws Throwable {
      String timeStamp = request.getParameter("time"); // do we load file
      Path path = request.getPath(); 
      Project project = workspace.createProject(path);
      ProjectFileSystem system = project.getFileSystem();
      File file = system.getFile(path);
      String name = project.getProjectName();
      List<BackupFile> files = manager.findAllBackups(file, name);
      
      for(BackupFile entry : files) {
         if(entry.getTimeStamp().equals(timeStamp)) {
            File backupFile = entry.getFile();
            return new FileInputStream(backupFile);
         }
      }
      return new FileInputStream(file);
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