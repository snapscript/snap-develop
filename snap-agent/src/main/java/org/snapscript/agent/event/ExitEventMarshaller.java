/*
 * ExitEventMarshaller.java December 2016
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

import static org.snapscript.agent.event.ProcessEventType.EXIT;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.snapscript.agent.ProcessMode;

public class ExitEventMarshaller implements ProcessEventMarshaller<ExitEvent> {

   @Override
   public ExitEvent fromMessage(MessageEnvelope message) throws IOException {
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
      ByteArrayInputStream buffer = new ByteArrayInputStream(array, offset, length);
      DataInputStream input = new DataInputStream(buffer);
      String process = input.readUTF();
      String type = input.readUTF();
      ProcessMode mode = ProcessMode.resolveMode(type);
      long duration = input.readLong();
      
      return new ExitEvent.Builder(process)
         .withDuration(duration)
         .withMode(mode)
         .build();
      
   }

   @Override
   public MessageEnvelope toMessage(ExitEvent value) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(buffer);
      ProcessMode mode = value.getMode();
      String process = value.getProcess();
      String type = mode.name();
      long duration = value.getDuration();
      
      output.writeUTF(process);
      output.writeUTF(type);
      output.writeLong(duration);
      output.flush();
      
      byte[] array = buffer.toByteArray();
      return new MessageEnvelope(EXIT.code, array, 0, array.length);
   }
}
