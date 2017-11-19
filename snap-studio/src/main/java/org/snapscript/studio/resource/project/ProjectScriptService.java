package org.snapscript.studio.resource.project;

import java.io.File;

import org.simpleframework.http.Cookie;
import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.socket.FrameChannel;
import org.simpleframework.http.socket.Session;
import org.simpleframework.http.socket.service.Service;
import org.snapscript.common.thread.ThreadPool;
import org.snapscript.core.Bug;
import org.snapscript.studio.command.CommandController;
import org.snapscript.studio.command.CommandListener;
import org.snapscript.studio.common.resource.display.DisplayPersister;
import org.snapscript.studio.core.BackupManager;
import org.snapscript.studio.core.ConnectListener;
import org.snapscript.studio.core.ProcessManager;
import org.snapscript.studio.core.Workspace;
import org.snapscript.studio.resource.tree.TreeContextManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProjectScriptService implements Service {
   
   private final DisplayPersister displayPersister;
   private final TreeContextManager treeManager;
   private final ProjectProblemFinder problemFinder;
   private final ConnectListener connectListener;
   private final ProcessManager processManager;
   private final BackupManager backupManager;
   private final Workspace workspace;
   private final String session;
   
   @Bug("le rubbish")
   public ProjectScriptService(
         ProcessManager processManager, 
         ConnectListener connectListener, 
         Workspace workspace, 
         BackupManager backupManager, 
         TreeContextManager treeManager, 
         DisplayPersister displayPersister,
         ThreadPool pool, 
         @Value("${session.id:SESSID}") String session) 
   {
      this.problemFinder = new ProjectProblemFinder(workspace, pool);
      this.displayPersister = displayPersister;
      this.treeManager = treeManager;
      this.backupManager = backupManager;
      this.connectListener = connectListener;
      this.workspace = workspace;
      this.processManager = processManager;
      this.session = session;
   }  
  
   @Override
   public void connect(Session connection) {
      Request request = connection.getRequest();    
      Path path = request.getPath(); // /connect/<project-name>
      
      try {
         FrameChannel channel = connection.getChannel();
         Project project = workspace.createProject(path);
         File projectPath = project.getProjectPath();
         String projectName = project.getProjectName();
         Cookie cookie = request.getCookie(session);
         String value = null;
         
         if(cookie != null) {
            value = cookie.getValue();
         }
         try {
            CommandListener commandListener = new CommandListener(
                  processManager, 
                  problemFinder, 
                  displayPersister,
                  channel, 
                  workspace.getLogger(), 
                  backupManager, 
                  treeManager,
                  project,
                  path, 
                  value);
            CommandController commandController = new CommandController(commandListener);

            channel.register(commandController);
            connectListener.connect(commandListener, path); // if there is a script then execute it
         } catch(Exception e) {
            workspace.getLogger().info("Could not connect " + path, e);
         }
      }catch(Exception e){
         workspace.getLogger().info("Error connecting " + path, e);
      }
      
   }
}