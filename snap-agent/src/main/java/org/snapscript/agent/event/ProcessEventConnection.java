package org.snapscript.agent.event;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProcessEventConnection {

   private final ProcessEventExecutor executor;
   private final ProcessEventConsumer consumer;
   private final ProcessEventProducer producer;

   public ProcessEventConnection(ProcessEventExecutor executor, InputStream input, OutputStream output, Closeable closeable) {
      this.consumer = new ProcessEventConsumer(input, closeable);
      this.producer = new ProcessEventProducer(output, closeable, executor);
      this.executor = executor;
   }

   public ProcessEventConsumer getConsumer() throws IOException {
      return consumer;
   }

   public ProcessEventProducer getProducer() throws IOException {
      return producer;
   }
   
   public ProcessEventExecutor getExecutor() throws IOException {
      return executor;
   }
}
