package org.snapscript.develop.socket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import junit.framework.TestCase;

import org.simpleframework.transport.Channel;
import org.simpleframework.transport.reactor.ExecutorReactor;
import org.simpleframework.transport.reactor.Reactor;
import org.snapscript.agent.event.MessageEnvelope;
import org.snapscript.agent.event.MessageEnvelopeWriter;
import org.snapscript.agent.event.PingEvent;
import org.snapscript.agent.event.ProcessEvent;
import org.snapscript.agent.event.ProcessEventAdapter;
import org.snapscript.agent.event.ProcessEventMarshaller;
import org.snapscript.agent.event.ProcessEventTimer;
import org.snapscript.agent.event.ProcessEventType;
import org.snapscript.agent.log.ConsoleLog;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.common.ThreadPool;
import org.snapscript.develop.tunnel.MessageEnvelopeCollector;
import org.snapscript.develop.tunnel.ProcessEventService;

public class MessageEnvelopeCollectorTest extends TestCase {
   
   public void testCollector() throws Exception {
      ThreadPool pool = new ThreadPool(5);
      ConsoleLog log = new ConsoleLog();
      ProcessLogger logger = new ProcessLogger(log, "TRACE");
      ProcessEventAdapter adapter = new ProcessEventAdapter();
      ProcessEventTimer timer = new ProcessEventTimer(adapter, logger);
      ProcessEventService router = new ProcessEventService(timer, logger, 7878);
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      StreamEventWriter writer = new StreamEventWriter(stream);
      PingEvent event = new PingEvent.Builder("agent-123456789")
         .withTime(11L)
         .build();
      
      writer.write(event);
      writer.write(event);
      
      byte[] data = stream.toByteArray();
      InputStream input = new ByteArrayInputStream(data);
      Channel channel = new StreamChannel(input, System.out);
      Reactor reactor = new ExecutorReactor(pool);
      MessageEnvelopeCollector collector = new MessageEnvelopeCollector(router, logger, reactor, pool, channel);
      
      collector.run();
   }
   
   public static class StreamEventWriter  {
      
      private final Map<Class, ProcessEventMarshaller> marshallers;
      private final MessageEnvelopeWriter writer;
      
      public StreamEventWriter(OutputStream output) {
         this.marshallers = new ConcurrentHashMap<Class, ProcessEventMarshaller>();
         this.writer = new MessageEnvelopeWriter(output, output);
      }
      
      public void write(ProcessEvent event) throws Exception {
         Class type = event.getClass();
         
         if (!marshallers.containsKey(type)) {
            ProcessEventType[] eventTypes = ProcessEventType.values();
   
            for (ProcessEventType eventType : eventTypes) {
               ProcessEventMarshaller marshaller = eventType.marshaller.newInstance();
               marshallers.put(eventType.event, marshaller);
            }
         }
         ProcessEventMarshaller marshaller = marshallers.get(type);
         MessageEnvelope message = marshaller.toMessage(event);
   
         writer.write(message);
      }
   }
}
