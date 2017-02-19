/*
 * ExitEvent.java December 2016
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

import org.snapscript.agent.ProcessMode;

public class ExitEvent implements ProcessEvent {

   private final ProcessMode mode;
   private final String process;
   private final long duration;

   private ExitEvent(Builder builder) {
      this.duration = builder.duration;
      this.process = builder.process;
      this.mode = builder.mode;
   }
   
   public ProcessMode getMode() {
      return mode;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   public long getDuration() { // execute time
      return duration;
   }

   public static class Builder {
      
      private ProcessMode mode;
      private String process;
      private long duration;
      
      public Builder(String process) {
         this.process = process;
      }
      
      public Builder withMode(ProcessMode mode) {
         this.mode = mode;
         return this;
      }

      public Builder withProcess(String process) {
         this.process = process;
         return this;
      }

      public Builder withDuration(long duration) {
         this.duration = duration;
         return this;
      }
      
      public ExitEvent build(){
         return new ExitEvent(this);
      }
      
   }
}
