package org.snapscript.studio.tunnel;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import org.simpleframework.transport.Channel;
import org.simpleframework.transport.reactor.ExecutorReactor;
import org.simpleframework.transport.reactor.Reactor;
import org.snapscript.agent.event.MessageEnvelope;
import org.snapscript.agent.event.ProcessEvent;
import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.agent.event.ProcessEventListener;
import org.snapscript.agent.event.ProcessEventMarshaller;
import org.snapscript.agent.event.ProcessEventType;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.common.thread.ThreadPool;

public class ProcessEventService implements MessageEnvelopeProcessor, ProcessEventChannel {

   private final Map<Integer, ProcessEventMarshaller> marshallers;
   private final Map<String, ProcessEventChannel> channels;
   private final ProcessEventRouter router;
   private final ProcessLogger logger;
   private final Executor executor;
   private final Reactor reactor;
   
   public ProcessEventService(ProcessEventListener listener, ProcessLogger logger) throws IOException {
      this(listener, logger, 5);
   }

   public ProcessEventService(ProcessEventListener listener, ProcessLogger logger, int threads) throws IOException {
      this.marshallers = new ConcurrentHashMap<Integer, ProcessEventMarshaller>();
      this.channels = new ConcurrentHashMap<String, ProcessEventChannel>();
      this.router = new ProcessEventRouter(listener);
      this.executor = new ThreadPool(threads);
      this.reactor = new ExecutorReactor(executor);
      this.logger = logger;
   }
   
   public void connect(Channel channel) throws Exception {
      MessageEnvelopeCollector collector = new MessageEnvelopeCollector(this, logger, reactor, executor, channel);
      reactor.process(collector);
   }

   @Override
   public boolean send(ProcessEvent event) throws Exception {
      String process = event.getProcess();
      ProcessEventChannel channel = channels.get(process);
      
      if(channel == null) {
         throw new IllegalArgumentException("No channel for " + process);
      }
      return channel.send(event);
   }

   @Override
   public boolean sendAsync(ProcessEvent event) throws Exception {
      String process = event.getProcess();
      ProcessEventChannel channel = channels.get(process);

      if(channel == null) {
         throw new IllegalArgumentException("No channel for " + process);
      }
      return channel.sendAsync(event);
   }

   @Override
   public void close(String reason) throws Exception {
      return;
   }
   
   @Override
   public void process(ProcessEventChannel channel, MessageEnvelope message) throws Exception {
      if(marshallers.isEmpty()) {
         ProcessEventType[] events = ProcessEventType.values();
         
         for(ProcessEventType event : events) {
            ProcessEventMarshaller marshaller = event.marshaller.newInstance();
            marshallers.put(event.code, marshaller);
         }
      }
      int code = message.getCode();
      ProcessEventMarshaller marshaller = marshallers.get(code);

      if(marshaller == null) {
         throw new IllegalStateException("Could not find marshaller for " + code);
      }
      ProcessEvent event = marshaller.fromMessage(message);
      String process = event.getProcess();
      
      channels.put(process, channel);
      router.route(channel, event);
   }
}