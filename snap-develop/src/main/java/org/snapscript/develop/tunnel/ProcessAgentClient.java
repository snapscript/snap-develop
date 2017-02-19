/*
 * ProcessAgentClient.java December 2016
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

import java.io.OutputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.simpleframework.transport.Channel;
import org.snapscript.agent.event.ProcessEvent;
import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.agent.event.ProcessEventProducer;
import org.snapscript.agent.log.ProcessLogger;

public class ProcessAgentClient implements ProcessEventChannel {

   private final ProcessEventProducer producer;
   private final ProcessLogger logger;
   private final OutputStream stream;
   private final AtomicBoolean open;
   
   public ProcessAgentClient(ProcessLogger logger, Executor executor, Channel channel) {
      this.stream = new ChannelOutputStream(channel);
      this.producer = new ProcessEventProducer(logger, stream, stream, executor);
      this.open = new AtomicBoolean(true);
      this.logger = logger;
   }
   
   @Override
   public boolean send(ProcessEvent event) throws Exception {
      String process = event.getProcess();
      
      try {
         producer.produce(event);
         return true;
      } catch(Exception e) {
         logger.info(process + ": Error sending event", e);
         close(process + ": Error sending event " + event + ": " +e);
      }
      return false;
   }

   @Override
   public boolean sendAsync(ProcessEvent event) throws Exception {
      String process = event.getProcess();
      
      try {
         Future<Boolean> future = producer.produceAsync(event);
         return future.get();
      } catch(Exception e) {
         logger.info(process + ": Error sending event", e);
         close(process + ": Error sending async event " + event + ": " +e);
      }
      return false;
   }

   @Override
   public void close(String reason) throws Exception {
      try {
         if(open.compareAndSet(true, false)) {
            producer.close(reason);
         }
      } catch(Exception e) {
         logger.info("Error closing socket", e);
      } 
   }
}
