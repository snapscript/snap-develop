/*
 * StepEvent.java December 2016
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

public class StepEvent implements ProcessEvent {
   
   public static final int RUN = 0;
   public static final int STEP_IN = 1;
   public static final int STEP_OVER = 2;
   public static final int STEP_OUT = 3;
   
   private final String process;
   private final String thread;
   private final int type;
   
   private StepEvent(Builder builder) {
      this.process = builder.process;
      this.thread = builder.thread;
      this.type = builder.type;
   }

   @Override
   public String getProcess() {
      return process;
   }
   
   public String getThread() {
      return thread;
   }
   
   public int getType() {
      return type;
   }
   
   public static class Builder {
      
      private String process;
      private String thread;
      private int type;
      
      public Builder(String process) {
         this.process = process;
      }

      public Builder withProcess(String process) {
         this.process = process;
         return this;
      }

      public Builder withThread(String thread) {
         this.thread = thread;
         return this;
      }

      public Builder withType(int type) {
         this.type = type;
         return this;
      }
      
      public StepEvent build(){
         return new StepEvent(this);
      }
   }

}
