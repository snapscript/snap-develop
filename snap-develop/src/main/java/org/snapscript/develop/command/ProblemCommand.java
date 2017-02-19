/*
 * ProblemCommand.java December 2016
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

public class ProblemCommand implements Command {

   private String project;
   private String description;
   private String resource;
   private int line;
   private long time;
   
   public ProblemCommand() {
      super();
   }
   
   public ProblemCommand(String project, String description, String resource, long time, int line) {
      this.description = description;
      this.resource = resource;
      this.project = project;
      this.time = time;
      this.line = line;
   }

   public String getProject() {
      return project;
   }

   public void setProject(String project) {
      this.project = project;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getResource() {
      return resource;
   }

   public void setResource(String resource) {
      this.resource = resource;
   }

   public int getLine() {
      return line;
   }

   public void setLine(int line) {
      this.line = line;
   }

   public long getTime() {
      return time;
   }

   public void setTime(long time) {
      this.time = time;
   }
} 
