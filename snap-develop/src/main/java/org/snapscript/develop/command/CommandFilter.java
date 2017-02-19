/*
 * CommandFilter.java December 2016
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

import java.util.concurrent.atomic.AtomicReference;

import org.snapscript.agent.event.ProcessEvent;
import org.snapscript.agent.event.ProcessEventFilter;

public class CommandFilter implements ProcessEventFilter {

   private final AtomicReference<String> attachment;
   
   public CommandFilter() {
      this.attachment = new AtomicReference<String>();
   }
   
   public String getFocus(){
      return attachment.get();
   }

   @Override
   public void setFocus(String process) {
      attachment.set(process);
   }
   
   public boolean isFocused(ProcessEvent event) {
      String process = event.getProcess();
      String focus = attachment.get();
      
      if(focus != null) {
         return process.equals(focus);
      }
      return false;
   }
   
   public void clearFocus() {
      attachment.set(null);
   }

}
