package org.snapscript.agent.event;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProcessEventConnection {

   private final ProcessEventExecutor executor;
   private final MessageEnvelopReader consumer;
   private final MessageEnvelopeWriter producer;
   private final MessageConnection connection;

   public ProcessEventConnection(ProcessEventExecutor executor, InputStream input, OutputStream output) {
      this.connection = new MessageConnection(input, output);
      this.consumer = new MessageEnvelopReader(input, connection);
      this.producer = new MessageEnvelopeWriter(output, connection);
      this.executor = executor;
   }

   public ProcessEventConsumer getConsumer() throws IOException {
      return new ProcessEventConsumer(consumer);
   }

   public ProcessEventProducer getProducer() throws IOException {
      return new ProcessEventProducer(executor, producer);
   }

   private static class MessageConnection implements Closeable {

      private final OutputStream output;
      private final InputStream input;

      public MessageConnection(InputStream input, OutputStream output) {
         this.output = output;
         this.input = input;
      }

      @Override
      public void close() throws IOException {
         input.close();
         output.close();
      }

   }
}
