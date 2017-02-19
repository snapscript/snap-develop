/*
 * MessageEnvelopeDecoder.java December 2016
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

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.snapscript.agent.event.MessageEnvelope;
import org.snapscript.agent.event.MessageEnvelopeReader;

public class MessageEnvelopeDecoder {

   public static int decodeInt(byte[] data, int off, int length) throws IOException {
      InputStream stream = new ByteArrayInputStream(data, off, length);
      DataInput input = new DataInputStream(stream);
      
      return input.readInt();
   }
   
   public static MessageEnvelope decodeMessage(byte[] data, int off, int length) throws IOException {
      InputStream stream = new ByteArrayInputStream(data, off, length);
      DataInput input = new DataInputStream(stream);
      
      return MessageEnvelopeReader.read(input);
   }
}
