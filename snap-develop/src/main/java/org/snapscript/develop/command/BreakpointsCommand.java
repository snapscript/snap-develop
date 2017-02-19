/*
 * BreakpointsCommand.java December 2016
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

public class BreakpointsCommand implements Command {

   protected Map<String, Map<Integer, Boolean>> breakpoints;
   protected String project;
   
   public BreakpointsCommand() {
      this.breakpoints = new HashMap<String, Map<Integer, Boolean>>();
   }
   
   public BreakpointsCommand(String project, Map<String, Map<Integer, Boolean>> breakpoints) {
      this.breakpoints = breakpoints;
      this.project = project;
   }

   public Map<String, Map<Integer, Boolean>> getBreakpoints() {
      return breakpoints;
   }

   public void setBreakpoints(Map<String, Map<Integer, Boolean>> breakpoints) {
      this.breakpoints = breakpoints;
   }

   public String getProject() {
      return project;
   }

   public void setProject(String project) {
      this.project = project;
   }
}
