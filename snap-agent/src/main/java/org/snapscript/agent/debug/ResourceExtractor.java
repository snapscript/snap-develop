/*
 * ResourceExtractor.java December 2016
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

package org.snapscript.agent.debug;

public class ResourceExtractor {
   
   private static final String SOURCE_SUFFIX = ".snap";

   public static String extractModule(String path) {
      int length = path.length();
      
      if(path.endsWith(SOURCE_SUFFIX)) {
         path = path.substring(0, length - 5);
      }
      if(path.startsWith("/")) {
         path = path.substring(1);
      }
      return path.replace('/', '.');
   }
   
   public static String extractResource(String module) {
      String path = module;
      
      if(!path.startsWith("/")) {
         path = "/" + path;
      }
      if(!path.endsWith(SOURCE_SUFFIX)) {
         path = path.replace('.', '/');
         path = path + SOURCE_SUFFIX;
      }
      return path;
   }
}
