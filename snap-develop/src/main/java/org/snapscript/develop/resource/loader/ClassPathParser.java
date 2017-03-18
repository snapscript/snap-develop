/*
 * ClassPathParser.java December 2016
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

package org.snapscript.develop.resource.loader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassPathParser {
   
   private final Map<String, String> cache;
   
   public ClassPathParser() {
      this.cache = new ConcurrentHashMap<String, String>();
   }

   public String parse(String path) {
      String type = cache.get(path);
      
      if(type == null) {
         int length = path.length();
         
         if(length > 7) {
            type = path.substring(1, length -6);
            type = type.replace('/', '.');
            
            cache.put(path, type);
         }
      }
      return type;
   }
}
