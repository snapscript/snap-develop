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

import org.simpleframework.http.Cookie;
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
import org.snapscript.develop.configuration.ConfigurationClassLoader;
import org.snapscript.develop.http.tree.TreeContextManager;

public class ProjectScriptService implements Service {
   
   private final TreeContextManager treeManager;
   private final ProjectProblemFinder problemFinder;
   private final ConnectListener connectListener;
   private final ProjectBuilder projectBuilder;
   private final ProcessManager processManager;
   private final ProcessLogger processLogger;
   private final BackupManager backupManager;
   private final String session;
   
   public ProjectScriptService(ProcessManager processManager, ConnectListener connectListener, ConfigurationClassLoader loader, ProcessLogger processLogger, ProjectBuilder projectBuilder, BackupManager backupManager, TreeContextManager treeManager, ThreadPool pool, String session) {
      this.problemFinder = new ProjectProblemFinder(projectBuilder, processLogger, pool);
      this.treeManager = treeManager;
      this.backupManager = backupManager;
      this.connectListener = connectListener;
      this.projectBuilder = projectBuilder;
      this.processLogger = processLogger;
      this.processManager = processManager;
      this.session = session;
   }  
  
   @Override
   public void connect(Session connection) {
      Request request = connection.getRequest();    
      Path path = request.getPath(); // /connect/<project-name>
      
      try {
         FrameChannel channel = connection.getChannel();
         Project project = projectBuilder.createProject(path);
         File projectPath = project.getProjectPath();
         String projectName = project.getProjectName();
         Cookie cookie = request.getCookie(session);
         String value = null;
         
         if(cookie != null) {
            value = cookie.getValue();
         }
         try {
            CommandListener commandListener = new CommandListener(processManager, problemFinder, channel, processLogger, backupManager, treeManager, path, projectPath, projectName, value);
            CommandController commandController = new CommandController(commandListener);

            channel.register(commandController);
            connectListener.connect(commandListener, path); // if there is a script then execute it
         } catch(Exception e) {
            processLogger.info("Could not connect " + path, e);
         }
      }catch(Exception e){
         processLogger.info("Error connecting " + path, e);
      }
      
   }
}
