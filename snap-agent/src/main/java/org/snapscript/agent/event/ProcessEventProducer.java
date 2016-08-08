package org.snapscript.agent.event;

import java.util.HashMap;
import java.util.Map;

public class ProcessEventProducer {

   private final Map<Class, ProcessEventMarshaller> marshallers;
   private final MessageEnvelopeWriter writer;
   
   public ProcessEventProducer(MessageEnvelopeWriter writer) {
      this.marshallers = new HashMap<Class, ProcessEventMarshaller>();
      this.writer = writer;
   }
   
   public void produce(ProcessEvent object) throws Exception {
      Class type = object.getClass();
      
      if(!marshallers.containsKey(type)) {
         ProcessEventType[] events = ProcessEventType.values();
         
         for(ProcessEventType event : events) {
            ProcessEventMarshaller marshaller = event.marshaller.newInstance();
            marshallers.put(event.event, marshaller);
         }
      }
      ProcessEventMarshaller marshaller = marshallers.get(type);
      MessageEnvelope message = marshaller.toMessage(object);
      
      writer.write(message);
   }
}
