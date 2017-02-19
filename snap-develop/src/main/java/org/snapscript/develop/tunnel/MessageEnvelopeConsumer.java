/*
 * MessageEnvelopeConsumer.java December 2016
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

package org.snapscript.develop.tunnel;

import java.util.concurrent.Executor;

import org.simpleframework.transport.ByteCursor;
import org.simpleframework.transport.Channel;
import org.snapscript.agent.event.MessageEnvelope;
import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.agent.log.ProcessLogger;
              
public class MessageEnvelopeConsumer {
   
   private final int HEADER_LENGTH = 16; // int,int,long
   
   private ProcessEventChannel channel;
   private ProcessEventService router;
   private byte[] buffer;
   private int remaining;
   private int count;
   
   public MessageEnvelopeConsumer(ProcessEventService router, ProcessLogger logger, Executor executor, Channel channel) {
      this.channel = new ProcessAgentClient(logger, executor, channel);
      this.buffer = new byte[1024];
      this.router = router;
      
   }
   
   public void consume(ByteCursor cursor) throws Exception {
      if(cursor.isReady()) {
         if(count < HEADER_LENGTH) {
            int length = cursor.read(buffer, count, HEADER_LENGTH - count);
            
            if(length > 0) {
               count += length;
            }
            if(count == HEADER_LENGTH) {
               remaining = MessageEnvelopeDecoder.decodeInt(buffer, 0, HEADER_LENGTH);

               if(remaining + HEADER_LENGTH > buffer.length) {
                  byte[] expand = new byte[remaining + HEADER_LENGTH];
                  System.arraycopy(buffer, 0, expand, 0, HEADER_LENGTH);
                  buffer = expand;
               }
            }
         }
         if(remaining > 0) {
            int length = cursor.read(buffer, count, remaining);
            
            if(length > 0) {
               remaining -= length;
               count += length;
               
               if(remaining == 0) {
                  MessageEnvelope envelope = MessageEnvelopeDecoder.decodeMessage(buffer, 0, count);
                  count = 0;
                  remaining = 0;
                  router.process(channel, envelope);
               }
            }  
         }
      }
   }
}
