package org.snapscript.agent.event;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class ProcessEventProducer {
   
   private final Map<Class, ProcessEventMarshaller> marshallers;
   private final MessageEnvelopeWriter writer;
   private final Executor executor;
   
   public ProcessEventProducer(ProcessEventExecutor executor, MessageEnvelopeWriter writer) {
      this.marshallers = new ConcurrentHashMap<Class, ProcessEventMarshaller>();
      this.executor = executor;
      this.writer = writer;
   }
   
   public void produce(ProcessEvent event) throws Exception {
      SendTask task = new SendTask(event);
      //executor.execute(task);
      task.call();
   }

   public Future<Boolean> produceAsync(ProcessEvent event) throws Exception {
      SendTask task = new SendTask(event);
      FutureTask<Boolean> future = new FutureTask<Boolean>(task);

      executor.execute(future);
      return future;
   }
   
   public void close() throws Exception {
      SendTask task = new SendTask(null);
      FutureTask<Boolean> future = new FutureTask<Boolean>(task);

      executor.execute(future);
      future.get();
   }
   
   private class SendTask implements Callable<Boolean> {
      
      private final ProcessEvent event;
      
      public SendTask(ProcessEvent event) {
         this.event = event;
      }
      
      @Override
      public Boolean call() throws Exception {
         if(event == null) { // close event
            writer.close();
            return true;
         }
         return write();
      }
   
      public Boolean write() throws Exception {
         Class type = event.getClass();
   
         if (!marshallers.containsKey(type)) {
            ProcessEventType[] events = ProcessEventType.values();
   
            for (ProcessEventType event : events) {
               ProcessEventMarshaller marshaller = event.marshaller.newInstance();
               marshallers.put(event.event, marshaller);
            }
         }
         ProcessEventMarshaller marshaller = marshallers.get(type);
         MessageEnvelope message = marshaller.toMessage(event);
   
         writer.write(message);
         return true;
      
      }
   }

}
