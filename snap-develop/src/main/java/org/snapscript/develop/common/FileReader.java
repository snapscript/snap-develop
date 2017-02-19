/*
 * FileReader.java December 2016
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileReader {

   public static String readText(File file) throws Exception {
      byte[] binary = readBinary(file);
      return new String(binary, "UTF-8");
   }
   
   public static byte[] readBinary(File file) throws Exception {
      if(file.exists() && file.isFile()) {
         InputStream source = new FileInputStream(file);
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         byte[] data = new byte[1024];
         int count = 0;
         
         try {
            while((count = source.read(data)) != -1) {
               buffer.write(data, 0, count);
            }
            return buffer.toByteArray();
         } finally {
            source.close();
         }
      }
      throw new IOException("Resource "  + file + " is a directory");   
   }
}
