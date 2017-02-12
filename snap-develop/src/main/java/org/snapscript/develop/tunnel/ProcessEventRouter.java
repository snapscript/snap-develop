package org.snapscript.develop.tunnel;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import org.simpleframework.transport.Channel;
import org.simpleframework.transport.reactor.ExecutorReactor;
import org.simpleframework.transport.reactor.Reactor;
import org.snapscript.agent.event.BeginEvent;
import org.snapscript.agent.event.BreakpointsEvent;
import org.snapscript.agent.event.BrowseEvent;
import org.snapscript.agent.event.EvaluateEvent;
import org.snapscript.agent.event.ExecuteEvent;
import org.snapscript.agent.event.ExitEvent;
import org.snapscript.agent.event.FaultEvent;
import org.snapscript.agent.event.MessageEnvelope;
import org.snapscript.agent.event.PingEvent;
import org.snapscript.agent.event.PongEvent;
import org.snapscript.agent.event.ProcessEvent;
import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.agent.event.ProcessEventListener;
import org.snapscript.agent.event.ProcessEventMarshaller;
import org.snapscript.agent.event.ProcessEventType;
import org.snapscript.agent.event.ProfileEvent;
import org.snapscript.agent.event.RegisterEvent;
import org.snapscript.agent.event.ScopeEvent;
import org.snapscript.agent.event.StepEvent;
import org.snapscript.agent.event.SyntaxErrorEvent;
import org.snapscript.agent.event.WriteErrorEvent;
import org.snapscript.agent.event.WriteOutputEvent;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.common.ThreadPool;

public class ProcessEventRouter implements MessageEnvelopeProcessor, ProcessEventChannel {

   private final Map<Integer, ProcessEventMarshaller> marshallers;
   private final Map<String, ProcessEventChannel> channels;
   private final ProcessEventListener listener;
   private final ProcessLogger logger;
   private final Executor executor;
   private final Reactor reactor;
   private final int port;
   
   public ProcessEventRouter(ProcessEventListener listener, ProcessLogger logger, int port) throws IOException {
      this(listener, logger, port, 5);
   }

   public ProcessEventRouter(ProcessEventListener listener, ProcessLogger logger, int port, int threads) throws IOException {
      this.marshallers = new ConcurrentHashMap<Integer, ProcessEventMarshaller>();
      this.channels = new ConcurrentHashMap<String, ProcessEventChannel>();
      this.executor = new ThreadPool(threads);
      this.reactor = new ExecutorReactor(executor);
      this.listener = listener;
      this.logger = logger;
      this.port = port;
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
   public void close() throws Exception {
      return;
   }

   @Override
   public int port() throws Exception {
      return port;
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
      
      if(event instanceof ExitEvent) {
         listener.onExit(channel, (ExitEvent)event);
      } else if(event instanceof ExecuteEvent) {
         listener.onExecute(channel, (ExecuteEvent)event);                  
      } else if(event instanceof RegisterEvent) {
         listener.onRegister(channel, (RegisterEvent)event);
      } else if(event instanceof SyntaxErrorEvent) {
         listener.onSyntaxError(channel, (SyntaxErrorEvent)event);
      } else if(event instanceof WriteErrorEvent) {
         listener.onWriteError(channel, (WriteErrorEvent)event);
      } else if(event instanceof WriteOutputEvent) {
         listener.onWriteOutput(channel, (WriteOutputEvent)event);
      } else if(event instanceof PingEvent) {
         listener.onPing(channel, (PingEvent)event);
      } else if(event instanceof PongEvent) {
         listener.onPong(channel, (PongEvent)event);
      } else if(event instanceof ScopeEvent) {
         listener.onScope(channel, (ScopeEvent)event);
      } else if(event instanceof BreakpointsEvent) {
         listener.onBreakpoints(channel, (BreakpointsEvent)event);
      } else if(event instanceof BeginEvent) {
         listener.onBegin(channel, (BeginEvent)event);
      } else if(event instanceof StepEvent) {
         listener.onStep(channel, (StepEvent)event);
      } else if(event instanceof BrowseEvent) {
         listener.onBrowse(channel, (BrowseEvent)event);
      } else if(event instanceof EvaluateEvent) {
         listener.onEvaluate(channel, (EvaluateEvent)event);                  
      } else if(event instanceof ProfileEvent) {
         listener.onProfile(channel, (ProfileEvent)event);
      } else if(event instanceof EvaluateEvent) {
         listener.onEvaluate(channel, (EvaluateEvent)event);
      } else if(event instanceof FaultEvent) {
         listener.onFault(channel, (FaultEvent)event);
      }
   }
}
