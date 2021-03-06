package org.snapscript.studio.service.socket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import junit.framework.TestCase;

import org.simpleframework.transport.Channel;
import org.simpleframework.transport.reactor.ExecutorReactor;
import org.simpleframework.transport.reactor.Reactor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snapscript.common.thread.ThreadPool;
import org.snapscript.studio.agent.event.MessageEnvelope;
import org.snapscript.studio.agent.event.MessageEnvelopeWriter;
import org.snapscript.studio.agent.event.PingEvent;
import org.snapscript.studio.agent.event.ProcessEvent;
import org.snapscript.studio.agent.event.ProcessEventAdapter;
import org.snapscript.studio.agent.event.ProcessEventMarshaller;
import org.snapscript.studio.agent.event.ProcessEventTimer;
import org.snapscript.studio.agent.event.ProcessEventType;
import org.snapscript.studio.agent.log.Log;
import org.snapscript.studio.agent.log.LogLevel;
import org.snapscript.studio.agent.log.LogLogger;
import org.snapscript.studio.agent.log.TraceLogger;
import org.snapscript.studio.common.log.LoggerLog;
import org.snapscript.studio.service.message.AsyncEventExchanger;
import org.snapscript.studio.service.message.MessageEnvelopeCollector;

public class MessageEnvelopeCollectorTest extends TestCase {
   
   private static final Logger LOG = LoggerFactory.getLogger(MessageEnvelopeCollectorTest.class);
   
   public void testCollector() throws Exception {
      ThreadPool pool = new ThreadPool(5);
      Log log = new LoggerLog(LOG);
      TraceLogger logger = new LogLogger(log, LogLevel.TRACE);
      ProcessEventAdapter adapter = new ProcessEventAdapter();
      ProcessEventTimer timer = new ProcessEventTimer(adapter, logger);
      AsyncEventExchanger router = new AsyncEventExchanger(timer, 7878);
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
      MessageEnvelopeCollector collector = new MessageEnvelopeCollector(router, reactor, pool, channel, "xx");
      
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
