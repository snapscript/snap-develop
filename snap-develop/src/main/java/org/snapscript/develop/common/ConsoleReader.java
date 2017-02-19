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

package org.snapscript.develop.common;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;

public class ConsoleReader {

   private final LineNumberReader parser;
   private final StringBuilder builder;
   private final InputStream buffer;
   private final Reader reader;

   public ConsoleReader(InputStream source) {
      this.buffer = new BufferedInputStream(source);
      this.reader = new InputStreamReader(buffer);
      this.parser = new LineNumberReader(reader);
      this.builder = new StringBuilder();
   }

   public String readAll() throws IOException {
      while (true) {
         String line = parser.readLine();

         if (line != null) {
            builder.append("\r\n");
            builder.append(line);
         } else {
            break;
         }
      }
      return builder.toString();
   }

   public String readLine() throws IOException {
      return parser.readLine();
   }
}
