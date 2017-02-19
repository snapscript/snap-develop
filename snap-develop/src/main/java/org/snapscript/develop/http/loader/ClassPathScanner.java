/*
 * ClassPathScanner.java December 2016
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

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;

public class ClassPathScanner {

   public static File findLocation(Class type) throws Exception {
      String file = extractName(type);
      URL location = type.getResource(file);

      if (location == null) {
         throw new IllegalStateException("Could not find '" + file + "'");
      }
      String resource = location.toString();

      if (resource.startsWith("file:")) {
         return convertFromFile(type, resource);
      }
      if (resource.startsWith("jar:file:")) {
         return convertFromJar(resource);
      }
      throw new IllegalStateException("Could not parse '" + resource + "'");
   }
   
   private static File convertFromFile(Class type, String resource) throws Exception {
      String directory = extractDirectory(type);
      URI location = URI.create(resource);
      String file = location.getRawSchemeSpecificPart();
      int index = file.indexOf(directory);
      
      if(index != -1) {
         file = file.substring(0, index);
      }
      return new File(file);
   }
   
   private static File convertFromJar(String resource) throws Exception {
      int index = resource.indexOf("!");

      if (index == -1) {
         throw new IllegalStateException("Could not determine source from '" + resource + "'");
      }
      String local = resource.substring(9, index);
      String decoded = URLDecoder.decode(local, "UTF-8");

      return new File(decoded);
   }
   
   private static String extractDirectory(Class type) throws Exception {
      String name = type.getName();
      int index = name.lastIndexOf('.');
     
      if(index != -1) {
         name = name.substring(0, index);
      }
      return name.replace('.', '/');
   }

   private static String extractName(Class type) throws Exception {
      String name = type.getName();
      int index = name.lastIndexOf('.');

      if (index != -1) {
         name = name.substring(index + 1);
      }
      return name + ".class";

   }
}
