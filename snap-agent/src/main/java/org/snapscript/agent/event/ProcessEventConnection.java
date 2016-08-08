package org.snapscript.agent.event;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProcessEventConnection {

   private final MessageEnvelopReader consumer;
   private final MessageEnvelopeWriter producer;
   
   public ProcessEventConnection(InputStream input, OutputStream output) {
      this.consumer = new MessageEnvelopReader(input);
      this.producer = new MessageEnvelopeWriter(output);
   }
   
   public ProcessEventConsumer getConsumer() throws IOException {
      return new ProcessEventConsumer(consumer);
   }
   
   public ProcessEventProducer getProducer() throws IOException {
      return new ProcessEventProducer(producer);
      
   }
}
