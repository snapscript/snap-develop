package org.snapscript.agent.event;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import org.snapscript.common.ThreadBuilder;
import org.snapscript.common.ThreadPool;

public class ProcessEventProducer {
   
   private final Map<Class, ProcessEventMarshaller> marshallers;
   private final MessageEnvelopeWriter writer;
   private final CountDownLatch latch;
   private final Executor executor;
   
   public ProcessEventProducer(ProcessEventExecutor executor, MessageEnvelopeWriter writer) {
      this.marshallers = new ConcurrentHashMap<Class, ProcessEventMarshaller>();
      this.latch = new CountDownLatch(1);
      this.executor = executor;
      this.writer = writer;
   }
   
   public void produce(ProcessEvent event) throws Exception {
      SendTask task = new SendTask(event);
      executor.execute(task);
   }
   
   public void close() throws Exception {
      CloseTask task = new CloseTask(latch);
      executor.execute(task);
      latch.await(); // make sure all events are flushed
   }
   
   private class CloseTask implements Runnable {
      
      private final CountDownLatch latch;
      
      public CloseTask(CountDownLatch latch) {
         this.latch = latch;
      }
      
      @Override
      public void run() {
         latch.countDown();
      }
   }
   
   private class SendTask implements Runnable {
      
      private final ProcessEvent event;
      
      public SendTask(ProcessEvent event) {
         this.event = event;
      }
      
      @Override
      public void run() {
         try {
            Class type = event.getClass();
            
            if(!marshallers.containsKey(type)) {
               ProcessEventType[] events = ProcessEventType.values();
               
               for(ProcessEventType event : events) {
                  ProcessEventMarshaller marshaller = event.marshaller.newInstance();
                  marshallers.put(event.event, marshaller);
               }
            }
            ProcessEventMarshaller marshaller = marshallers.get(type);
            MessageEnvelope message = marshaller.toMessage(event);
            
            writer.write(message);
         } catch(Exception e) {
            return;
         }
      }
   }
}
