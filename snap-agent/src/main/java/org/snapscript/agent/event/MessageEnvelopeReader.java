/*
 * MessageEnvelopeReader.java December 2016
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
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MessageEnvelopeReader {

   public final DataInputStream stream;
   public final Closeable closeable;
   
   public MessageEnvelopeReader(InputStream stream, Closeable closeable) {
      this.stream = new DataInputStream(stream);
      this.closeable = closeable;
   }
   
   public synchronized MessageEnvelope read() throws IOException {
      return read(stream);
   }
   
   public static MessageEnvelope read(DataInput input) throws IOException {
      int length = input.readInt();
      int type = input.readInt();
      long expect = input.readLong();
      byte[] array = new byte[length];
      
      try {
         input.readFully(array);
      } catch(Exception e) {
         throw new IllegalStateException("Could not read message of type " + type + " with length " + length, e);
      }
      long check  = MessageChecker.check(array, 0, length);
      
      if(check != expect) {
         throw new IllegalStateException("Message of type " + type + " did not match checksum " + check);
      }
      return new MessageEnvelope(type, array, 0, length);
   }
   
   public synchronized void close() throws IOException {
      closeable.close();
   }
}
