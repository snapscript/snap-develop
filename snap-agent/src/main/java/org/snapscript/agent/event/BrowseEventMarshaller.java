/*
 * BrowseEventMarshaller.java December 2016
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

import static org.snapscript.agent.event.ProcessEventType.BROWSE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class BrowseEventMarshaller implements ProcessEventMarshaller<BrowseEvent>{

   @Override
   public BrowseEvent fromMessage(MessageEnvelope message) throws IOException {
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
      Set<String> expand = new HashSet<String>();
      ByteArrayInputStream buffer = new ByteArrayInputStream(array, offset, length);
      DataInputStream input = new DataInputStream(buffer);
      String process = input.readUTF();
      String thread = input.readUTF();
      int count = input.readInt();
      
      for(int i = 0; i < count; i++) {
         String path = input.readUTF();
         expand.add(path);
      }
      return new BrowseEvent.Builder(process)
         .withThread(thread)
         .withExpand(expand)
         .build();
   }

   @Override
   public MessageEnvelope toMessage(BrowseEvent event) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(buffer);
      Set<String> expand = event.getExpand();
      String process = event.getProcess();
      String thread = event.getThread();
      int count = expand.size();
      
      output.writeUTF(process);
      output.writeUTF(thread);
      output.writeInt(count);
      
      for(String name : expand) {
         output.writeUTF(name);
      }
      output.flush();
      
      byte[] array = buffer.toByteArray();
      return new MessageEnvelope(BROWSE.code, array, 0, array.length);
   }

}
