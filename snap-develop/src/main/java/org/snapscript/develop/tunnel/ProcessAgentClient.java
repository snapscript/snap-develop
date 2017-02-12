package org.snapscript.develop.tunnel;

import java.io.OutputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.simpleframework.transport.Channel;
import org.snapscript.agent.event.ProcessEvent;
import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.agent.event.ProcessEventProducer;
import org.snapscript.agent.log.ProcessLogger;

public class ProcessAgentClient implements ProcessEventChannel {

   private final ProcessEventProducer producer;
   private final ProcessLogger logger;
   private final OutputStream stream;
   private final AtomicBoolean open;
   
   public ProcessAgentClient(ProcessLogger logger, Executor executor, Channel channel) {
      this.stream = new ChannelOutputStream(channel);
      this.producer = new ProcessEventProducer(stream, stream, executor);
      this.open = new AtomicBoolean(true);
      this.logger = logger;
   }
   
   @Override
   public boolean send(ProcessEvent event) throws Exception {
      String process = event.getProcess();
      
      try {
         producer.produce(event);
         return true;
      } catch(Exception e) {
         logger.info(process + ": Error sending event", e);
         close();
      }
      return false;
   }

   @Override
   public boolean sendAsync(ProcessEvent event) throws Exception {
      String process = event.getProcess();
      
      try {
         Future<Boolean> future = producer.produceAsync(event);
         return future.get();
      } catch(Exception e) {
         logger.info(process + ": Error sending event", e);
         close();
      }
      return false;
   }

   @Override
   public void close() throws Exception {
      try {
         if(open.compareAndSet(true, false)) {
            producer.close();
         }
         producer.close();
      } catch(Exception e) {
         logger.info("Error closing socket", e);
      } 
   }
}
