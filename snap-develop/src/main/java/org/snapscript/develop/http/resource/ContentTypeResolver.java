/*
 * ContentTypeResolver.java December 2016
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

package org.snapscript.develop.http.resource;

import java.util.Map;
import java.util.Set;

import org.snapscript.common.Cache;
import org.snapscript.common.LeastRecentlyUsedCache;

public class ContentTypeResolver {

   private final Cache<String, String> cache;
   private final Map<String, String> types;

   public ContentTypeResolver(Map<String, String> types) {
      this(types, 10000);
   }
   
   public ContentTypeResolver(Map<String, String> types, int capacity) {
      this.cache = new LeastRecentlyUsedCache<String, String>(capacity);
      this.types = types;
   }

   public String matchPath(String path) {
      Set<String> expressions = types.keySet();
      String token = path.toLowerCase();

      for (String expression : expressions) {         
         if (token.matches(expression)) {
            String type = types.get(expression);
            
            if(type != null) {             
               return type;
            }
         }
      }
      return "application/octet-stream";
   }   

   public String resolveType(String path) {
      String result = cache.fetch(path);
      
      if(result == null) {
         String type = matchPath(path);
         
         if(type != null) {
            cache.cache(path, type);
            return type;
         }
      }
      return result;
   }
}
