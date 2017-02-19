/*
 * SyntaxErrorEventMarshaller.java December 2016
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

import static org.snapscript.agent.event.ProcessEventType.SYNTAX_ERROR;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SyntaxErrorEventMarshaller implements ProcessEventMarshaller<SyntaxErrorEvent> {

   @Override
   public SyntaxErrorEvent fromMessage(MessageEnvelope message) throws IOException {
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
      ByteArrayInputStream buffer = new ByteArrayInputStream(array, offset, length);
      DataInputStream input = new DataInputStream(buffer);
      String process = input.readUTF();
      String resource = input.readUTF();
      String description = input.readUTF();
      int line = input.readInt();
      
      return new SyntaxErrorEvent.Builder(process)
         .withResource(resource)
         .withDescription(description)
         .withLine(line)
         .build();
   }

   @Override
   public MessageEnvelope toMessage(SyntaxErrorEvent event) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(buffer);
      String process = event.getProcess();
      String resource = event.getResource();
      String description = event.getDescription();
      int line = event.getLine();
      
      output.writeUTF(process);
      output.writeUTF(resource);
      output.writeUTF(description);
      output.writeInt(line);
      output.flush();
      
      byte[] array = buffer.toByteArray();
      return new MessageEnvelope(SYNTAX_ERROR.code, array, 0, array.length);
   }
}

