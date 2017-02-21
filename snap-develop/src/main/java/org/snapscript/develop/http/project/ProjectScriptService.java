/*
 * ProjectScriptService.java December 2016
 *
 * Copyright (C) 2016, Niall Gallagher <niallg@users.sf.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

package org.snapscript.develop.http.project;

import java.io.File;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.socket.FrameChannel;
import org.simpleframework.http.socket.Session;
import org.simpleframework.http.socket.service.Service;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.common.ThreadPool;
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
   private final ProcessLogger logger;
   private final BackupManager manager;
   
   public ProjectScriptService(ProcessManager engine, ConnectListener listener, ConfigurationClassLoader loader, ProcessLogger logger, ProjectBuilder builder, BackupManager manager, ThreadPool pool) {
      this.compiler = new ProjectProblemFinder(builder, logger, pool);
      this.loader = new TypeNodeScanner(builder, loader, logger, pool);
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
            logger.info("Could not connect " + path, e);
         }
      }catch(Exception e){
         logger.info("Error connecting " + path, e);
      }
      
   }
}
