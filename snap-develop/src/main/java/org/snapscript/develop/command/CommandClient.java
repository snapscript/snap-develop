/*
 * CommandClient.java December 2016
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

package org.snapscript.develop.command;

import java.util.Map;
import java.util.Set;

import org.simpleframework.http.socket.FrameChannel;
import org.snapscript.agent.profiler.ProfileResult;

public class CommandClient {
   
   private final CommandWriter writer;
   private final FrameChannel channel;
   private final String project;
   
   public CommandClient(FrameChannel channel, String project) {
      this.writer = new CommandWriter();
      this.channel = channel;
      this.project = project;
   } 
   
   public void sendScope(String process, Map<String, Map<String, String>> local, Map<String, Map<String, String>> evaluation, String thread, String stack, String instruction, String status, String resource, int line, int depth, int key, int change) throws Exception {
      ScopeCommand command = new ScopeCommand(process, local, evaluation, thread, stack, instruction, status, resource, line, depth, key, change);
      String message = writer.write(command);

      channel.send(message);
   }
   
   public void sendProfile(String process, Set<ProfileResult> results) throws Exception {
      ProfileCommand command = new ProfileCommand(process, results);
      String message = writer.write(command);
      
      channel.send(message);
   }
   
   public void sendBegin(String process, String resource, long duration) throws Exception {
      BeginCommand command = new BeginCommand(process, resource, duration);
      String message = writer.write(command);
      
      channel.send(message);
   }
   
   public void sendSyntaxError(String resource, String description, long time, int line) throws Exception {
      ProblemCommand command = new ProblemCommand(project, description, resource, time, line);
      String message = writer.write(command);
      
      channel.send(message);
   }
   
   public void sendPrintError(String process, String text) throws Exception {
      PrintErrorCommand command = new PrintErrorCommand(process, text);
      String message = writer.write(command);
      
      channel.send(message);
   }
   
   public void sendPrintOutput(String process, String text) throws Exception {
      PrintOutputCommand command = new PrintOutputCommand(process, text);
      String message = writer.write(command);
      
      channel.send(message);
   }
   
   public void sendProcessExit(String process) throws Exception {
      ExitCommand command = new ExitCommand(process);
      String message = writer.write(command);
      
      channel.send(message);
   }
   
   public void sendStatus(String process, String system, String project, String resource, long time, boolean running, boolean focus) throws Exception {
      StatusCommand command = new StatusCommand(process, system, project, resource, time, running, focus);
      String message = writer.write(command);

      channel.send(message);
   }
   
   public void sendProcessTerminate(String process) throws Exception {
      TerminateCommand command = new TerminateCommand(process);
      String message = writer.write(command);
      
      channel.send(message);
   }
   
   public void sendAlert(String resource, String text) throws Exception {
      AlertCommand command = new AlertCommand(resource, text);
      String message = writer.write(command);
      
      channel.send(message);
   }
   
   public void sendReloadTree() throws Exception {
      ReloadTreeCommand command = new ReloadTreeCommand();
      String message = writer.write(command);
      
      channel.send(message);
   }
}
