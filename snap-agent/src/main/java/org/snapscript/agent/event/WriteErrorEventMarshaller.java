/*
 * WriteErrorEventMarshaller.java December 2016
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

import static org.snapscript.agent.event.ProcessEventType.WRITE_ERROR;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class WriteErrorEventMarshaller implements ProcessEventMarshaller<WriteErrorEvent> {

   @Override
   public WriteErrorEvent fromMessage(MessageEnvelope message) throws IOException {
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
      ByteArrayInputStream buffer = new ByteArrayInputStream(array, offset, length);
      DataInputStream input = new DataInputStream(buffer);
      String process = input.readUTF();
      int size = input.readInt();
      byte[] chunk = new byte[size];
      
      input.readFully(chunk, 0, size);
      
      return new WriteErrorEvent.Builder(process)
         .withData(chunk)
         .withOffset(0)
         .withLength(size)
         .build();
   }

   @Override
   public MessageEnvelope toMessage(WriteErrorEvent event) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(buffer);
      String process = event.getProcess();
      byte[] chunk = event.getData();
      int length = event.getLength();
      int offset = event.getOffset();
      
      output.writeUTF(process);
      output.writeInt(length);
      output.write(chunk, offset, length);
      output.flush();
      
      byte[] array = buffer.toByteArray();
      return new MessageEnvelope(WRITE_ERROR.code, array, 0, array.length);
   }
}
