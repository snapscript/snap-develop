/*
 * ExecuteCommand.java December 2016
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

import java.util.HashMap;
import java.util.Map;

public class ExecuteCommand implements Command {

   private Map<String, Map<Integer, Boolean>> breakpoints;
   private String project;
   private String resource;
   private String system;
   private String source;
   
   public ExecuteCommand() {
      this.breakpoints = new HashMap<String, Map<Integer, Boolean>>();
   }
   
   public ExecuteCommand(String project, String system, String resource, String source, Map<String, Map<Integer, Boolean>> breakpoints) {
      this.breakpoints = breakpoints;
      this.resource = resource;
      this.project = project;
      this.system = system;
      this.source = source;
   }

   public Map<String, Map<Integer, Boolean>> getBreakpoints() {
      return breakpoints;
   }

   public void setBreakpoints(Map<String, Map<Integer, Boolean>> breakpoints) {
      this.breakpoints = breakpoints;
   }
   
   public String getSystem() {
      return system;
   }
   
   public void setSystem(String system) {
      this.system = system;
   }

   public String getProject() {
      return project;
   }

   public void setProject(String project) {
      this.project = project;
   }

   public String getResource() {
      return resource;
   }

   public void setResource(String resource) {
      this.resource = resource;
   }

   public String getSource() {
      return source;
   }

   public void setSource(String source) {
      this.source = source;
   }
}
