package org.snapscript.agent.debug;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.agent.event.ScopeEvent;
import org.snapscript.core.Context;
import org.snapscript.core.Module;
import org.snapscript.core.Scope;
import org.snapscript.core.error.ThreadStack;
import org.snapscript.core.trace.Trace;
import org.snapscript.core.trace.TraceListener;
import org.snapscript.core.trace.TraceType;

public class SuspendInterceptor implements TraceListener {

   private final ProcessEventChannel channel;
   private final ThreadProgressLocal monitor;
   private final BreakpointMatcher matcher;
   private final AtomicInteger counter;
   private final SuspendController latch;
   private final String process;
   
   public SuspendInterceptor(ProcessEventChannel channel, BreakpointMatcher matcher, SuspendController latch, String process) {
      this.monitor = new ThreadProgressLocal();
      this.counter = new AtomicInteger();
      this.matcher = matcher;
      this.channel = channel;
      this.process = process;
      this.latch = latch;
   }

   @Override
   public void before(Scope scope, Trace trace) {
      ThreadProgress progress = monitor.get();
      TraceType type = trace.getType();
      Module module = trace.getModule();
      String resource = module.getPath();
      int line = trace.getLine();
      
      if(matcher.match(resource, line) || progress.suspend(type)) { 
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
