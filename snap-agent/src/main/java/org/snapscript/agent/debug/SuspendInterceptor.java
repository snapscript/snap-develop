/*
 * SuspendInterceptor.java December 2016
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

package org.snapscript.agent.debug;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.agent.event.ScopeEvent;
import org.snapscript.core.Context;
import org.snapscript.core.Module;
import org.snapscript.core.Path;
import org.snapscript.core.Scope;
import org.snapscript.core.stack.ThreadStack;
import org.snapscript.core.trace.Trace;
import org.snapscript.core.trace.TraceType;

public class SuspendInterceptor extends TraceAdapter {

   private final ProcessEventChannel channel;
   private final ThreadProgressLocal monitor;
   private final AtomicInteger counter;
   private final SuspendController latch;
   private final String process;
   
   public SuspendInterceptor(ProcessEventChannel channel, BreakpointMatcher matcher, SuspendController latch, String process) {
      this.monitor = new ThreadProgressLocal(matcher);
      this.counter = new AtomicInteger();
      this.channel = channel;
      this.process = process;
      this.latch = latch;
   }

   @Override
   public void before(Scope scope, Trace trace) {
      ThreadProgress progress = monitor.get();
      TraceType type = trace.getType();
      Module module = scope.getModule();
      Path source = trace.getPath();
      String resource = source.getPath();
      int line = trace.getLine();
      
      if(progress.isSuspendBefore(trace)) { 
         try {
            String thread = Thread.currentThread().getName();
            int count = counter.getAndIncrement();
            int depth = progress.currentDepth();
            Context context = module.getContext();
            ThreadStack stack = context.getStack();
            String path = ResourceExtractor.extractResource(resource);
            ThreadStackGenerator generator = new ThreadStackGenerator(stack);
            String threads = generator.generate();
            ScopeExtractor extractor = new ScopeExtractor(context, scope);
            ScopeEventBuilder builder = new ScopeEventBuilder(extractor, type, process, thread, threads, path, line, depth, count);
            ScopeNotifier notifier = new ScopeNotifier(builder);
            ScopeEvent suspend = builder.suspendEvent();
            ScopeEvent resume = builder.resumeEvent();
            
            progress.clear(); // clear config
            channel.send(suspend);
            notifier.start();
            suspend(notifier, extractor, resource, line);
            channel.send(resume);
         } catch(Exception e) {
            e.printStackTrace();
         }
      }
      progress.beforeInstruction(type);
   }

   @Override
   public void after(Scope scope, Trace trace) {
      ThreadProgress progress = monitor.get();
      TraceType type = trace.getType();
      Module module = scope.getModule();
      Path source = trace.getPath();
      String resource = source.getPath();
      int line = trace.getLine();
      
      if(progress.isSuspendAfter(trace)) { 
         try {
            String thread = Thread.currentThread().getName();
            int count = counter.getAndIncrement();
            int depth = progress.currentDepth();
            Context context = module.getContext();
            ThreadStack stack = context.getStack();
            String path = ResourceExtractor.extractResource(resource);
            ThreadStackGenerator generator = new ThreadStackGenerator(stack);
            String threads = generator.generate();
            ScopeExtractor extractor = new ScopeExtractor(context, scope);
            ScopeEventBuilder builder = new ScopeEventBuilder(extractor, type, process, thread, threads, path, line, depth, count);
            ScopeNotifier notifier = new ScopeNotifier(builder);
            ScopeEvent suspend = builder.suspendEvent();
            ScopeEvent resume = builder.resumeEvent();
            
            progress.clear(); // clear config
            channel.send(suspend);
            notifier.start();
            suspend(notifier, extractor, resource, line);
            channel.send(resume);
         } catch(Exception e) {
            e.printStackTrace();
         }
      }
      progress.afterInstruction(type);
   }
   
   private void suspend(ScopeNotifier notifier, ScopeBrowser browser, String resource, int line) {
      ResumeType type = latch.suspend(notifier, browser);
      ThreadProgress step = monitor.get();
      
      step.resume(type);
   }
   
   private class ScopeNotifier extends Thread implements ResumeListener {
      
      private final ScopeEventBuilder builder;
      private final AtomicBoolean active;
      
      public ScopeNotifier(ScopeEventBuilder builder) {
         this.active = new AtomicBoolean(true);
         this.builder = builder;
      }

      @Override
      public void run() {
         try {
            while(active.get()) {
               Thread.sleep(400);
               
               if(active.get()) {
                  ScopeEvent event = builder.suspendEvent();
                  channel.send(event);
               }
            }
         } catch(Exception e) {
            e.printStackTrace();
         } finally {
            active.set(false);
         }
      }

      @Override
      public void resume(String thread) {
         active.set(false);
      }
   }

}
