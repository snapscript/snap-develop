/*
 * StatusCommand.java December 2016
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

public class StatusCommand implements Command {

   private String project;
   private String resource;
   private String process;
   private String system;
   private boolean focus;
   private boolean running;
   private long time;
   
   public StatusCommand() {
      super();
   }
   
   public StatusCommand(String process, String system, String project, String resource, long time, boolean running, boolean focus) {
      this.running = running;
      this.process = process;
      this.resource = resource;
      this.project = project;
      this.system = system;
      this.focus = focus;
      this.time = time;
   }
   
   public long getTime() {
      return time;
   }
   
   public void setTime(long time) {
      this.time = time;
   }
   
   public String getProject() {
      return project;
   }

   public void setProject(String project) {
      this.project = project;
   }

   public String getSystem() {
      return system;
   }
   
   public void setSystem(String system) {
      this.system = system;
   }

   public String getProcess() {
      return process;
   }

   public void setProcess(String process) {
      this.process = process;
   }

   public String getResource() {
      return resource;
   }

   public void setResource(String resource) {
      this.resource = resource;
   }

   public boolean isRunning() {
      return running;
   }

   public void setRunning(boolean running) {
      this.running = running;
   }
   
   public boolean isFocus() {
      return focus;
   }

   public void setFocus(boolean focus) {
      this.focus = focus;
   }
}