/*
 * StepCommand.java December 2016
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

public class StepCommand implements Command {

   private static enum StepType {
      RUN,
      STEP_IN,
      STEP_OVER,
      STEP_OUT;
   }
   
   private String thread;
   private StepType type;
   
   public StepCommand(String thread, StepType type) {
      this.thread = thread;
      this.type = type;
   }
   
   public String getThread() {
      return thread;
   }
   
   public boolean isRun() {
      return type == StepType.RUN;
   }
   
   public boolean isStepIn() {
      return type == StepType.STEP_IN;
   }
   
   public boolean isStepOver() {
      return type == StepType.STEP_OVER;
   }
   
   public boolean isStepOut() {
      return type == StepType.STEP_OUT;
   }
}
