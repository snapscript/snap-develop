/*
 * ClassResourceLoader.java December 2016
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

package org.snapscript.develop.http.loader;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassResourceLoader {
   
   private final Map<String, byte[]> cache;
   private final ClassPathParser parser;
   private final ClassFilter filter;
   
   public ClassResourceLoader(String prefix) {
      this.cache = new ConcurrentHashMap<String, byte[]>();
      this.parser = new ClassPathParser();
      this.filter = new ClassFilter(prefix);
   }

   public byte[] loadClass(String path) throws Exception {
      byte[] data = cache.get(path);
      
      if(data == null) {
         String type = parser.parse(path);
         
         if(filter.accept(type)) {
            data = loadResource(path);
            
            if(data != null) {
               cache.put(path, data);
            }
         }
      }
      return data;
   }
   
   private byte[] loadResource(String path) throws Exception {
      String location = path.substring(1);
      ClassLoader loader = ClassLoader.getSystemClassLoader();
      InputStream input = loader.getResourceAsStream(location);
      
      if(input != null) {
         ByteArrayOutputStream output = new ByteArrayOutputStream();
         byte[] buffer = new byte[1024];
         int count = 0;
         
         while((count = input.read(buffer)) != -1) {
            output.write(buffer, 0, count);
         }
         output.close();
         input.close();
         return output.toByteArray();
      }
      return null;
   }
}
