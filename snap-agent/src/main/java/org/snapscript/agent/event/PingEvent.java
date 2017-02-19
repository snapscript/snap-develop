/*
 * PingEvent.java December 2016
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

public class PingEvent implements ProcessEvent {

   private final String process;
   private final long time;
   
   private PingEvent(Builder builder) {
      this.process = builder.process;
      this.time = builder.time;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   public long getTime() {
      return time;
   }

   public static class Builder {
      
      private String process;
      private long time;
      
      public Builder(String process){
         this.process = process;
      }
      
      public Builder withProcess(String process) {
         this.process = process;
         return this;
      }
      
      public Builder withTime(long time) {
         this.time = time;
         return this;
      } 
      
      public PingEvent build(){
         return new PingEvent(this);
      }
   }
}
