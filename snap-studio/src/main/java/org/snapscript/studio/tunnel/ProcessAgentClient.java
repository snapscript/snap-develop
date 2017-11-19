package org.snapscript.studio.tunnel;

import java.io.OutputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.simpleframework.transport.Channel;
import org.snapscript.studio.agent.event.ProcessEvent;
import org.snapscript.studio.agent.event.ProcessEventChannel;
import org.snapscript.studio.agent.event.ProcessEventProducer;
import org.snapscript.studio.agent.log.ProcessLogger;

public class ProcessAgentClient implements ProcessEventChannel {

   private final ProcessEventProducer producer;
   private final ProcessLogger logger;
   private final OutputStream stream;
   private final AtomicBoolean open;
   
   public ProcessAgentClient(ProcessLogger logger, Executor executor, Channel channel) {
      this.stream = new ChannelOutputStream(channel);
      this.producer = new ProcessEventProducer(logger, stream, stream, executor);
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
         close(process + ": Error sending event " + event + ": " +e);
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
         close(process + ": Error sending async event " + event + ": " +e);
      }
      return false;
   }

   @Override
   public void close(String reason) throws Exception {
      try {
         if(open.compareAndSet(true, false)) {
            producer.close(reason);
         }
      } catch(Exception e) {
         logger.info("Error closing socket", e);
      } 
   }
}