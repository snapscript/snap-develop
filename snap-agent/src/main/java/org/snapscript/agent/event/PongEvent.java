/*
 * PongEvent.java December 2016
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

public class PongEvent implements ProcessEvent {

   private final String project;
   private final String process;
   private final String resource;
   private final String system;
   private final boolean running;

   public PongEvent(Builder builder) {
      this.resource = builder.resource;
      this.process = builder.process;
      this.running = builder.running;
      this.project = builder.project;
      this.system = builder.system;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   public String getProject() {
      return project;
   }

   public String getSystem() {
      return system;
   }
   
   public String getResource() {
      return resource;
   }
   
   public boolean isRunning() {
      return running;
   }
   
   public static class Builder {
      
      private String project;
      private String process;
      private String resource;
      private String system;
      private boolean running;
   
      public Builder(String process) {
         this.process = process;
      }

      public Builder withProject(String project) {
         this.project = project;
         return this;
      }

      public Builder withProcess(String process) {
         this.process = process;
         return this;
      }

      public Builder withResource(String resource) {
         this.resource = resource;
         return this;
      }

      public Builder withSystem(String system) {
         this.system = system;
         return this;
      }

      public Builder withRunning(boolean running) {
         this.running = running;
         return this;
      }
      
      public PongEvent build() {
         return new PongEvent(this);
      }
   }
}
