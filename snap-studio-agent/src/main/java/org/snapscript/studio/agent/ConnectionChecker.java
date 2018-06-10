package org.snapscript.studio.agent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.snapscript.common.thread.ThreadBuilder;
import org.snapscript.studio.agent.event.PingEvent;
import org.snapscript.studio.agent.event.PongEvent;
import org.snapscript.studio.agent.event.ProcessEventChannel;

public class ConnectionChecker {

   private final DebugContext context;
   private final ThreadFactory factory;
   private final HealthChecker checker;
   private final AtomicBoolean active;
   private final AtomicLong update;
   private final String process;
   private final String system;
   
   public ConnectionChecker(DebugContext context, Runnable task, String process, String system) {
      this.checker = new HealthChecker(task, 10000);
      this.factory = new ThreadBuilder();
      this.active = new AtomicBoolean();
      this.update = new AtomicLong();
      this.context = context;
      this.process = process;
      this.system = system;
   }
   
   public void update(ProcessEventChannel channel, PingEvent event) {
      RunMode mode = context.getMode();
      ExecuteLatch latch = context.getLatch();
      ExecuteState state = latch.getState();
      ExecuteStatus status = state.getStatus();
      ExecuteData data = state.getData();
      String project = data.getProject();
      String resource = data.getResource();
      
      long time = System.currentTimeMillis();
      
      try {
         PongEvent pong = new PongEvent.Builder(process)
            .withSystem(system)
            .withProject(project)
            .withResource(resource)
            .withStatus(status)
            .withTotalMemory(Runtime.getRuntime().totalMemory())
            .withUsedMemory(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())
            .withThreads(Thread.getAllStackTraces().size()) // this might be expensive
            .build();
         
         if(mode.isDetachRequired()) {
            if(!status.isFinished()) { // send pong only if still running
               if(!channel.send(pong)) {
                  TerminateHandler.terminate("Ping failed for " + process);
               } else {
                  update.set(time);
               }
            }
         } else {
            if(!channel.send(pong)) {
               TerminateHandler.terminate("Ping failed for " + process);
            } else {
               update.set(time);
            }
         }
      } catch(Exception e) {
         e.printStackTrace();
         TerminateHandler.terminate("Ping failed for " + process + " with " + e);
      }
   }
   
   public void start() {
      if(active.compareAndSet(false, true)) {
         Thread thread = factory.newThread(checker);
         thread.start();
      }
   }
   
   private class HealthChecker implements Runnable {
      
      private final Runnable task;
      private final long frequency;
      
      public HealthChecker(Runnable task, long frequency) {
         this.frequency = frequency;
         this.task = task;
      }
      
      @Override
      public void run() {
         try {
            while(true) {
               Thread.sleep(frequency);
               long last = update.get();
               long time = System.currentTimeMillis();
               long duration = time - last;
               
               if(duration > frequency) { // ensure pings are frequent
                  break;
               }
            }
         } catch(Exception e) {
            e.printStackTrace();
         } finally {
            task.run();
         }
      }
   }

}