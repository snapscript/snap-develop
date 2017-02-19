/*
 * ProfileResultUpdater.java December 2016
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

package org.snapscript.agent.profiler;

import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.agent.event.ProfileEvent;
import org.snapscript.common.ThreadBuilder;

public class ProfileResultUpdater implements Runnable {

   private final AtomicReference<String> reference;
   private final ProcessEventChannel channel;
   private final ProcessProfiler profiler;
   private final ThreadFactory factory;

   public ProfileResultUpdater(ProcessProfiler profiler, ProcessEventChannel channel) {
      this.reference = new AtomicReference<String>();
      this.factory = new ThreadBuilder();
      this.profiler = profiler;
      this.channel = channel;
   }
   
   public void start(String process) {
      if(reference.compareAndSet(null, process)) {
         Thread thread = factory.newThread(this);
         thread.start();
      }
   }

   @Override
   public void run() {
      long delay = 1000;
      
      while(true) {
         String process = reference.get();
         try {
            Thread.sleep(delay);
            Set<ProfileResult> results = profiler.lines(2000);
            ProfileEvent event = new ProfileEvent.Builder(process)
               .withResults(results)
               .build();
            
            channel.send(event);
         }catch(Exception e) {
            e.printStackTrace();
         }finally{
            delay = 5000;
         }
      }
   }
}
