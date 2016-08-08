package org.snapscript.develop.http.project;

import java.io.File;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.socket.FrameChannel;
import org.simpleframework.http.socket.Session;
import org.simpleframework.http.socket.service.Service;
import org.snapscript.agent.ConsoleLogger;
import org.snapscript.develop.BackupManager;
import org.snapscript.develop.ConnectListener;
import org.snapscript.develop.ProcessManager;
import org.snapscript.develop.command.CommandController;
import org.snapscript.develop.command.CommandListener;
import org.snapscript.develop.complete.TypeNodeScanner;
import org.snapscript.develop.configuration.ConfigurationClassLoader;

public class ProjectScriptService implements Service {
   
   private final ProjectProblemFinder compiler;
   private final TypeNodeScanner loader;
   private final ConnectListener listener;
   private final ProjectBuilder builder;
   private final ProcessManager engine;
   private final ConsoleLogger logger;
   private final BackupManager manager;
   
   public ProjectScriptService(ProcessManager engine, ConnectListener listener, ConfigurationClassLoader loader, ConsoleLogger logger, ProjectBuilder builder, BackupManager manager) {
      this.compiler = new ProjectProblemFinder(builder, logger);
      this.loader = new TypeNodeScanner(builder, loader, logger);
      this.manager = manager;
      this.listener = listener;
      this.builder = builder;
      this.logger = logger;
      this.engine = engine;
   }  
  
   @Override
   public void connect(Session connection) {
      Request request = connection.getRequest();    
      Path path = request.getPath(); // /connect/<project-name>
      
      try {
         FrameChannel channel = connection.getChannel();
         Project project = builder.createProject(path);
         File projectPath = project.getProjectPath();
         String projectName = project.getProjectName();
         
         try {
            CommandListener commandListener = new CommandListener(engine, compiler, loader, channel, logger, manager, path, projectPath, projectName);
            CommandController commandController = new CommandController(commandListener);

            channel.register(commandController);
            listener.connect(commandListener, path); // if there is a script then execute it
         } catch(Exception e) {
            logger.log("Could not connect " + path, e);
         }
      }catch(Exception e){
         logger.log("Error connecting " + path, e);
      }
      
   }
}
