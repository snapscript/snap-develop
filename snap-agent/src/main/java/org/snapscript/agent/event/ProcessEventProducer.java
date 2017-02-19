/*
 * ProcessEventProducer.java December 2016
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
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.snapscript.agent.log.ProcessLogger;

public class ProcessEventProducer {
   
   private final Map<Class, ProcessEventMarshaller> marshallers;
   private final MessageEnvelopeWriter writer;
   private final ProcessLogger logger;
   private final Executor executor;
   
   public ProcessEventProducer(ProcessLogger logger, OutputStream stream, Closeable closeable, Executor executor) {
      this.marshallers = new ConcurrentHashMap<Class, ProcessEventMarshaller>();
      this.writer = new MessageEnvelopeWriter(stream, closeable);
      this.executor = executor;
      this.logger = logger;
   }
   
   public void produce(ProcessEvent event) throws Exception {
      SendTask task = new SendTask(event);
      //executor.execute(task);
      task.call();
   }

   public Future<Boolean> produceAsync(ProcessEvent event) throws Exception {
      SendTask task = new SendTask(event);
      FutureTask<Boolean> future = new FutureTask<Boolean>(task);

      executor.execute(future);
      return future;
   }
   
   public void close(String reason) throws Exception {
      CloseTask task = new CloseTask(reason);
      FutureTask<Boolean> future = new FutureTask<Boolean>(task);

      executor.execute(future);
      future.get();
   }
   
   private class CloseTask implements Callable<Boolean> {
      
      private final Exception cause;
      private final String reason;
      
      public CloseTask(String reason) {
         this.cause = new Exception("Closing connection: " + reason);
         this.reason = reason;
      }
      
      @Override
      public Boolean call() throws Exception {
         try {
            logger.info("Closing connection: " + reason);
            cause.printStackTrace();
            writer.close();
         }catch(Exception e) {
            throw new IllegalStateException("Could not close writer: " + reason);
         }
         return true;
      }
   }
   
   private class SendTask implements Callable<Boolean> {
      
      private final ProcessEvent event;
      
      public SendTask(ProcessEvent event) {
         this.event = event;
      }
      
      @Override
      public Boolean call() throws Exception {
         Class type = event.getClass();
   
         if (!marshallers.containsKey(type)) {
            ProcessEventType[] events = ProcessEventType.values();
   
            for (ProcessEventType event : events) {
               ProcessEventMarshaller marshaller = event.marshaller.newInstance();
               marshallers.put(event.event, marshaller);
            }
         }
         ProcessEventMarshaller marshaller = marshallers.get(type);
         MessageEnvelope message = marshaller.toMessage(event);
   
         writer.write(message);
         return true;
      
      }
   }

}
