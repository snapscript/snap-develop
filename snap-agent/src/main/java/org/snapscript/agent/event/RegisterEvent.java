/*
 * RegisterEvent.java December 2016
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

public class RegisterEvent implements ProcessEvent {

   private final String process;
   private final String system;
   
   private RegisterEvent(Builder builder) {
      this.process = builder.process;
      this.system = builder.system;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   public String getSystem() {
      return system;
   }
   
   public static class Builder {
      
      private String process;
      private String system;
      
      public Builder(String process) {
         this.process = process;
      }

      public Builder withProcess(String process) {
         this.process = process;
         return this;
      }

      public Builder withSystem(String system) {
         this.system = system;
         return this;
      }
      
      public RegisterEvent build(){
         return new RegisterEvent(this);
      }
   }
}
