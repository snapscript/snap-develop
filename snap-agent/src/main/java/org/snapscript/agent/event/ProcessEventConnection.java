package org.snapscript.agent.event;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProcessEventConnection {

   private final ProcessEventExecutor executor;
   private final MessageEnvelopReader consumer;
   private final MessageEnvelopeWriter producer;

   public ProcessEventConnection(ProcessEventExecutor executor, InputStream input, OutputStream output, Closeable closeable) {
      this.consumer = new MessageEnvelopReader(input, closeable);
      this.producer = new MessageEnvelopeWriter(output, closeable);
      this.executor = executor;
   }

   public ProcessEventConsumer getConsumer() throws IOException {
      return new ProcessEventConsumer(consumer);
   }

   public ProcessEventProducer getProducer() throws IOException {
      return new ProcessEventProducer(executor, producer);
   }
}
