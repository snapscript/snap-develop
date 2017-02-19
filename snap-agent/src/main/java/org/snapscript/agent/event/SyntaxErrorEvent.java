/*
 * SyntaxErrorEvent.java December 2016
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

public class SyntaxErrorEvent implements ProcessEvent {

   private final String description;
   private final String resource;
   private final String process;
   private final int line;
   
   private SyntaxErrorEvent(Builder builder) {
      this.description = builder.description;
      this.process = builder.process;
      this.resource = builder.resource;
      this.line = builder.line;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   public String getDescription(){
      return description;
   }
      
   public String getResource() {
      return resource;
   }

   public int getLine() {
      return line;
   }
   
   public static class Builder {
      
      private String description;
      private String resource;
      private String process;
      private int line;
      
      public Builder(String process) {
         this.process = process;
      }

      public Builder withDescription(String description) {
         this.description = description;
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

      public Builder withLine(int line) {
         this.line = line;
         return this;
      }
      
      public SyntaxErrorEvent build(){
         return new SyntaxErrorEvent(this);
      }
      
      
   }
}
