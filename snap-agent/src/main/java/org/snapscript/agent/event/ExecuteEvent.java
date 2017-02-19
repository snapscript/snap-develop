/*
 * ExecuteEvent.java December 2016
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

package org.snapscript.agent.event;

import java.util.Map;

public class ExecuteEvent implements ProcessEvent {

   private final Map<String, Map<Integer, Boolean>> breakpoints;
   private final ExecuteData data;
   private final String project;
   private final String resource;
   private final String process;
   
   private ExecuteEvent(Builder builder) {
      this.data = new ExecuteData(builder.process, builder.project, builder.resource);
      this.breakpoints = builder.breakpoints;
      this.project = builder.project;
      this.resource = builder.resource;
      this.process = builder.process;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   public ExecuteData getData() {
      return data; 
   }
   
   public Map<String, Map<Integer, Boolean>> getBreakpoints() {
      return breakpoints;
   }
   
   public String getResource() {
      return resource;
   }
   
   public String getProject() {
      return project;
   }
   
   public static class Builder {
      
      private Map<String, Map<Integer, Boolean>> breakpoints;
      private String project;
      private String resource;
      private String process;
      
      public Builder(String process) {
         this.process = process;
      }

      public Builder withBreakpoints(Map<String, Map<Integer, Boolean>> breakpoints) {
         this.breakpoints = breakpoints;
         return this;
      }

      public Builder withProject(String project) {
         this.project = project;
         return this;
      }

      public Builder withResource(String resource) {
         this.resource = resource;
         return this;
      }

      public Builder withProcess(String process) {
         this.process = process;
         return this;
      }
      
      public ExecuteEvent build(){
         return new ExecuteEvent(this);
      }
   }
}
