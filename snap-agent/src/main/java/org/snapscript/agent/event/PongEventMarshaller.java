/*
 * PongEventMarshaller.java December 2016
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

import static org.snapscript.agent.event.ProcessEventType.PONG;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PongEventMarshaller implements ProcessEventMarshaller<PongEvent> {

   @Override
   public PongEvent fromMessage(MessageEnvelope message) throws IOException {
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
      ByteArrayInputStream buffer = new ByteArrayInputStream(array, offset, length);
      DataInputStream input = new DataInputStream(buffer);
      String process = input.readUTF();
      String system = input.readUTF();
      
      if(input.readBoolean()) {
         String project = input.readUTF();
         String resource = input.readUTF();
         
         return new PongEvent.Builder(process)
            .withSystem(system)
            .withProject(project)
            .withResource(resource)
            .withRunning(true)
            .build();
      }
      return new PongEvent.Builder(process)
         .withSystem(system)
         .withRunning(false)
         .build();
   }

   @Override
   public MessageEnvelope toMessage(PongEvent event) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(buffer);
      String process = event.getProcess();
      String resource = event.getResource();
      String project = event.getProject();
      String system = event.getSystem();
      
      output.writeUTF(process);
      output.writeUTF(system);
      
      if(event.isRunning()) {
         output.writeBoolean(true);
         output.writeUTF(project);
         output.writeUTF(resource);
      } else {
         output.writeBoolean(false);
      }
      output.flush();
      
      byte[] array = buffer.toByteArray();
      return new MessageEnvelope(PONG.code, array, 0, array.length);
   }
}

