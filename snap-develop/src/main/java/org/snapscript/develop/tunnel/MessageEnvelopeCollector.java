package org.snapscript.develop.tunnel;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.concurrent.Executor;

import org.simpleframework.transport.ByteCursor;
import org.simpleframework.transport.Channel;
import org.simpleframework.transport.reactor.Operation;
import org.simpleframework.transport.reactor.Reactor;
import org.simpleframework.transport.trace.Trace;
import org.snapscript.agent.log.ProcessLogger;

public class MessageEnvelopeCollector implements Operation {
   
   private final MessageEnvelopeConsumer consumer;
   private final ProcessLogger logger;
   private final Reactor reactor;
   private final Channel channel;
   
   public MessageEnvelopeCollector(ProcessEventRouter router, ProcessLogger logger, Reactor reactor, Executor executor, Channel channel) {
      this.consumer = new MessageEnvelopeConsumer(router, logger, executor, channel);
      this.reactor = reactor;
      this.channel = channel;
      this.logger = logger;
   }

   @Override
   public Trace getTrace() {
      return channel.getTrace();
   }

   @Override
   public SelectableChannel getChannel() {
      return channel.getSocket();
   }

   @Override
   public void run() {
      try {
         ByteCursor cursor = channel.getCursor();
         
         while(cursor.isReady()) {
            consumer.consume(cursor);
         }
         if(cursor.isOpen()) {
            reactor.process(this, SelectionKey.OP_READ);
         }
      }catch(Exception e) {
         logger.debug("Could not consume message", e);
         cancel(); // close the transport
      }
   }

   @Override
   public void cancel() {
      try {
         channel.close();
      }catch(Exception e) {
         logger.debug("Could not close transport", e);
      }
   }
}
