/*
 * ProcessTask.java December 2016
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

import java.util.SortedSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.snapscript.agent.event.BeginEvent;
import org.snapscript.agent.event.ExitEvent;
import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.agent.event.ProfileEvent;
import org.snapscript.agent.profiler.ProcessProfiler;
import org.snapscript.agent.profiler.ProfileResult;
import org.snapscript.agent.profiler.ProfileResultUpdater;
import org.snapscript.compile.Executable;
import org.snapscript.compile.ResourceCompiler;
import org.snapscript.core.Model;

public class ProcessTask implements Runnable {
   
   private final ProcessEventChannel client;
   private final ProcessContext context;
   private final ProcessMode mode;
   private final String resource;
   private final String project;
   private final Model model;
   
   public ProcessTask(ProcessEventChannel client, ProcessContext context, ProcessMode mode, Model model, String project, String resource) {
      this.client = client;
      this.resource = resource;
      this.project = project;
      this.context = context;
      this.model = model;
      this.mode = mode;
   }
   
   @Override
   public void run() {
      CountDownLatch latch = context.getLatch();
      String process = context.getProcess();
      
      // start and listen for the socket close
      long start = System.nanoTime();
      try {
         ProcessProfiler profiler = context.getProfiler();
         ResourceCompiler compiler = context.getCompiler();
         Executable executable = compiler.compile(resource);
         long middle = System.nanoTime();
         ProfileResultUpdater updater = new ProfileResultUpdater(profiler, client);
         BeginEvent event = new BeginEvent.Builder(process)
            .withProject(project)
            .withResource(resource)
            .withMode(context.getMode())
            .withDuration(TimeUnit.NANOSECONDS.toMillis(middle-start))
            .build();
         
         client.send(event);
         
         try {
            updater.start(process); // start sending profile events!!!
            middle = System.nanoTime();
            executable.execute(model); // execute the script
         } catch(Throwable e) {
            e.printStackTrace();
         }finally {
            try {
               long stop = System.nanoTime();
               System.err.flush(); // flush output to sockets
               System.out.flush();
               Thread.sleep(200);
               // should really be a heat map for the editor
               SortedSet<ProfileResult> lines = profiler.lines(200);
               System.err.flush();
               System.out.flush();
               
               ProfileEvent profileEvent = new ProfileEvent.Builder(process)
                  .withResults(lines)
                  .build();
               
               client.send(profileEvent);
               Thread.sleep(2000);
               System.err.close();
               System.out.close();
               
               ExitEvent exitEvent = new ExitEvent.Builder(process)
                  .withDuration(TimeUnit.NANOSECONDS.toMillis(stop-middle))
                  .withMode(context.getMode())
                  .build();
               
               client.send(exitEvent);
            } catch(Exception e) {
               e.printStackTrace();
            } finally {
               if(mode.isTerminateRequired()) {
                  ProcessTerminator.terminate("Task has finished executing"); // shutdown when finished
               }
               latch.countDown();
            }
         }
      } catch (Exception e) {
         System.err.println(ExceptionBuilder.build(e));
      } finally {
         if(mode.isTerminateRequired()) {
            ProcessTerminator.terminate("Task has finished executing"); // shutdown when finished
         }
         latch.countDown();
      }
   }
}
