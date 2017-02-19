/*
 * ProcessResourceExecutor.java December 2016
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

package org.snapscript.agent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

import org.snapscript.agent.event.ExecuteData;
import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.common.ThreadBuilder;
import org.snapscript.core.Model;

public class ProcessResourceExecutor {

   private final AtomicReference<ExecuteData> reference;
   private final ProcessContext context;
   private final ProcessMode mode;
   private final ThreadFactory factory;
   private final Model model;
   
   public ProcessResourceExecutor(ProcessContext context, ProcessMode mode, Model model) {
      this.reference = new AtomicReference<ExecuteData>();
      this.factory = new ThreadBuilder();
      this.context = context;
      this.model = model;
      this.mode = mode;
   }

   public void execute(ProcessEventChannel channel, String process, String project, String resource) {
      try {
         ExecuteData data = new ExecuteData(process, project, resource);
         ConsoleConnector connector = new ConsoleConnector(channel, process);
         ProcessTask task = new ProcessTask(channel, context, mode, model, project, resource);
         
         if(resource != null) {
            Thread thread = factory.newThread(task);
            
            if(reference.compareAndSet(null, data)) {
               connector.connect();
               thread.start();
            }
         }
      } catch(Exception e) {
         throw new IllegalStateException("Could not execute '" + resource + "' from project '" + project + "'", e);
      }
   }
   
   public ExecuteData get() {
      return reference.get();
   }
}
