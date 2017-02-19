/*
 * MessageEnvelopeWriter.java December 2016
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
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class MessageEnvelopeWriter {

   private final DataOutputStream stream;
   private final Closeable closeable;
   
   public MessageEnvelopeWriter(OutputStream stream, Closeable closeable) {
      this.stream = new DataOutputStream(stream);
      this.closeable = closeable;
   }
   
   public synchronized void write(MessageEnvelope message) throws IOException {
      write(message, stream);
      stream.flush();
   }
   
   public static void write(MessageEnvelope message, DataOutput output) throws IOException {
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
      int type = message.getCode();
      long check = MessageChecker.check(array, offset, length);
      
      output.writeInt(length); // length of the payload
      output.writeInt(type);
      output.writeLong(check);
      output.write(array, offset, length);
   }
   
   public synchronized void close() throws IOException {
      try {
         stream.flush();
      }finally {
         closeable.close();
      }
   }
}
