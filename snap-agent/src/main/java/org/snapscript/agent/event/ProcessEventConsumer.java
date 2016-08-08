package org.snapscript.agent.event;

import java.util.HashMap;
import java.util.Map;

public class ProcessEventConsumer {
   
   private final Map<Integer, ProcessEventMarshaller> marshallers;
   private final MessageEnvelopReader reader;

   public ProcessEventConsumer(MessageEnvelopReader reader) {
      this.marshallers = new HashMap<Integer, ProcessEventMarshaller>();
      this.reader = reader;
   }
   
   public ProcessEvent consume() throws Exception {
      if(marshallers.isEmpty()) {
         ProcessEventType[] events = ProcessEventType.values();
         
         for(ProcessEventType event : events) {
            ProcessEventMarshaller marshaller = event.marshaller.newInstance();
            marshallers.put(event.code, marshaller);
         }
      }
      MessageEnvelope message = reader.read();
      int code = message.getCode();
      ProcessEventMarshaller marshaller = marshallers.get(code);
      
      if(marshaller == null) {
         throw new IllegalStateException("Could not find marshaller for " + code);
      }
      return marshaller.fromMessage(message);
   }

}
