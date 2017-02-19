/*
 * ProcessEventConsumer.java December 2016
 *
 * Copyright (C) 2016, Niall Gallagher <niallg@users.sf.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

package org.snapscript.agent.event;

import java.io.Closeable;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ProcessEventConsumer {
   
   private final Map<Integer, ProcessEventMarshaller> marshallers;
   private final MessageEnvelopeReader reader;

   public ProcessEventConsumer(InputStream stream, Closeable closeable) {
      this.marshallers = new HashMap<Integer, ProcessEventMarshaller>();
      this.reader = new MessageEnvelopeReader(stream, closeable);
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
