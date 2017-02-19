/*
 * TemplateFinder.java December 2016
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

package org.snapscript.develop.http.resource.template;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

import org.snapscript.develop.http.resource.FileResolver;

public class TemplateFinder {

   private final FileResolver resolver;
   private final String suffix;
   private final String prefix;

   public TemplateFinder(FileResolver resolver, String prefix) {
      this(resolver, prefix, null);
   }
   
   public TemplateFinder(FileResolver resolver, String prefix, String suffix) {
      this.resolver = resolver;
      this.suffix = suffix;
      this.prefix = prefix;
   }
   
   public Reader findReader(String path) throws IOException {
      InputStream stream = findStream(path);
      
      if(stream != null) {
         return new InputStreamReader(stream, "UTF-8");
      }
      return null;
   } 
   
   public InputStream findStream(String path) throws IOException {
      String realPath = findPath(path);

      if (realPath != null) {
         return resolver.resolveStream(realPath);
      }
      return null;
   }  

   public String findPath(String path) throws IOException {
      List<String> searchPath = searchPath(path);

      for(String realPath : searchPath) {
         InputStream source = resolver.resolveStream(realPath);
         
         if(source != null) {
            return realPath;
         }
      }
      return null;
   }

   private List<String> searchPath(String path) throws IOException {
      if (suffix != null) {
         if (!path.endsWith(suffix)) {
            path = path + suffix;
         }
      }
      if (prefix != null) {
         String original = path;
         
         if (path.startsWith("/")) {
            path = path.substring(1);
         }         
         if (!path.startsWith(prefix)) {
            path = prefix + path;
         }
         return Arrays.asList(path, original);
      }
      return Arrays.asList(path);
   }
}
