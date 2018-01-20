package org.snapscript.studio.agent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.snapscript.common.thread.ThreadBuilder;
import org.snapscript.studio.agent.event.PingEvent;
import org.snapscript.studio.agent.event.PongEvent;
import org.snapscript.studio.agent.event.ProcessEventChannel;

public class ConnectionChecker {

   private final ProcessContext context;
   private final ThreadFactory factory;
   private final HealthChecker checker;
   private final AtomicBoolean active;
   private final AtomicLong update;
   private final String process;
   private final String system;
   
   public ConnectionChecker(ProcessContext context, String process, String system) {
      this.checker = new HealthChecker(10000);
      this.factory = new ThreadBuilder();
      this.active = new AtomicBoolean();
      this.update = new AtomicLong();
      this.context = context;
      this.process = process;
      this.system = system;
   }
   
   public void update(ProcessEventChannel channel, PingEvent event, String project, String resource, boolean debug) {
      PongEvent pong = new PongEvent.Builder(process)
         .withSystem(system)
         .withProject(project)
         .withResource(resource)
         .withRunning(resource != null)
         .withTotalMemory(Runtime.getRuntime().totalMemory())
         .withUsedMemory(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())
         .withThreads(Thread.getAllStackTraces().size()) // this might be expensive
         .withDebug(debug)
         .build();
      
      try {
         CountDownLatch latch = context.getLatch();
         ProcessMode mode = context.getMode();
         long time = System.currentTimeMillis();

         if(mode.isDetachRequired()) {
            long count = latch.getCount();

            if(count > 0) { // send pong only if still running
               if(!channel.send(pong)) {
                  ProcessTerminator.terminate("Ping failed for " + process);
               } else {
                  update.set(time);
               }
            }
         } else {
            if(!channel.send(pong)) {
               ProcessTerminator.terminate("Ping failed for " + process);
            } else {
               update.set(time);
            }
         }
      } catch(Exception e) {
         e.printStackTrace();
         ProcessTerminator.terminate("Ping failed for " + process + " with " + e);
      }
   }
   
   public void start() {
      if(active.compareAndSet(false, true)) {
         Thread thread = factory.newThread(checker);
         thread.start();
      }
   }
   
   private class HealthChecker implements Runnable {
      
      private final long frequency;
      
      public HealthChecker(long frequency) {
         this.frequency = frequency;
      }
      
      @Override
      public void run() {
         ProcessMode mode = context.getMode();

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
            if(mode.isTerminateRequired()) {
               ProcessTerminator.terminate("Connection checker timeout elapsed");
            }
         }
      }
   }

}