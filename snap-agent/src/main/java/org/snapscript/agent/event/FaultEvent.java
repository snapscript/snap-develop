/*
 * FaultEvent.java December 2016
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

import org.snapscript.agent.debug.ScopeVariableTree;

public class FaultEvent implements ProcessEvent {

   private final ScopeVariableTree variables;
   private final String process;
   private final String resource;
   private final String thread;
   private final String cause;
   private final int line;
   
   private FaultEvent(Builder builder) {
      this.variables = builder.variables;
      this.resource = builder.resource;
      this.process = builder.process;
      this.thread = builder.thread;
      this.cause = builder.cause;
      this.line = builder.line;
   }
   
   @Override
   public String getProcess() {
      return process;
   }

   public ScopeVariableTree getVariables() {
      return variables;
   }

   public String getCause() {
      return cause;
   }
   
   public String getResource() {
      return resource;
   }

   public String getThread() {
      return thread;
   }

   public int getLine() {
      return line;
   }
   
   public static class Builder {
      
      private ScopeVariableTree variables;
      private String process;
      private String resource;
      private String thread;
      private String cause;
      private int line;
      
      public Builder(String process) {
         this.process = process;
      }

      public Builder withVariables(ScopeVariableTree variables) {
         this.variables = variables;
         return this;
      }

      public Builder withCause(String cause) {
         this.cause = cause;
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

      public Builder withThread(String thread) {
         this.thread = thread;
         return this;
      }

      public Builder withLine(int line) {
         this.line = line;
         return this;
      }
      
      public FaultEvent build() {
         return new FaultEvent(this);
      }
   }
}
