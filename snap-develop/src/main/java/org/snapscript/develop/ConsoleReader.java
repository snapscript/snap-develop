/*
 * ConsoleReader.java December 2016
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

package org.snapscript.develop;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class ConsoleReader {

   public static String read(InputStream stream) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      
      while(true) {
         int next = stream.read();
         
         if(next == -1) {
            int length = buffer.size();
            
            if(length == 0) {
               throw new EOFException("Console has been closed");
            }
            return buffer.toString("UTF-8");
         }
         buffer.write(next);
         
         if(next == '\n') {
            return buffer.toString("UTF-8");
         }
      }
   }
}
