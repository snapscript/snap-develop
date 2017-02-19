/*
 * BeginCommand.java December 2016
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

public class BeginCommand implements Command {

   private String resource;
   private String process;
   private long duration;
   
   public BeginCommand() {
      super();
   }
   
   public BeginCommand(String process, String resource, long duration) {
      this.process = process;
      this.resource = resource;
      this.duration = duration;
   }

   public String getResource() {
      return resource;
   }

   public void setResource(String resource) {
      this.resource = resource;
   }

   public String getProcess() {
      return process;
   }

   public void setProcess(String process) {
      this.process = process;
   }

   public long getDuration() { // compile time
      return duration;
   }

   public void setDuration(long duration) {
      this.duration = duration;
   }
}
