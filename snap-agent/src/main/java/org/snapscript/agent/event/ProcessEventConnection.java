package org.snapscript.agent.event;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProcessEventConnection {

   private final MessageEnvelopReader consumer;
   private final MessageEnvelopeWriter producer;
   private final ProcessEventExecutor executor;
   
   public ProcessEventConnection(ProcessEventExecutor executor, InputStream input, OutputStream output) {
      this.consumer = new MessageEnvelopReader(input);
      this.producer = new MessageEnvelopeWriter(output);
      this.executor = executor;
   }
   
   public ProcessEventConsumer getConsumer() throws IOException {
      return new ProcessEventConsumer(consumer);
   }
   
   public ProcessEventProducer getProducer() throws IOException {
      return new ProcessEventProducer(executor, producer);
      
   }
}
