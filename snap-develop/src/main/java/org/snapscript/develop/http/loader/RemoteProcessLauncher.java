/*
 * RemoteProcessLauncher.java December 2016
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
import java.io.FileReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RemoteProcessLauncher {

   public static void main(String[] list) throws Exception {
      URI classes = URI.create(list[0]);
      String type = list[1];
      String dependencies = list[2];
      String prefix = list[3];
      String[] arguments = Arrays.copyOfRange(list, 4, list.length);
      
      start(classes, type, dependencies, prefix, arguments);
   }

   public static void start(URI classes, String name, String dependencies, String prefix, String[] arguments) throws Exception {
      ClassLoader parent = load(dependencies);
      URL[] path = new URL[]{classes.toURL()};
      URLClassLoader loader = new RemoteClassLoader(path, parent, prefix);
      Class type = loader.loadClass(name);
      Method method = type.getDeclaredMethod("main", String[].class);

      method.invoke(null, (Object)arguments);
   }
   
   public static ClassLoader load(String dependencies) throws Exception {
      FileReader source = new FileReader(dependencies);
      LineNumberReader reader = new LineNumberReader(source);
      List<File> files = new ArrayList<File>();
      
      try {
         ClassLoader loader = RemoteProcessLauncher.class.getClassLoader();
         String line = reader.readLine();
         
         while(line != null) {
            String token = line.trim();
            int length = token.length();
            
            if(length > 0) {
               File file = new File(token);
               files.add(file);
            }
            line = reader.readLine();
         }
         int size = files.size();
         
         if(size > 0) {
            URL[] array = new URL[size];
            
            for(int i = 0; i < size; i++){
               File file = files.get(i);
               URI location = file.toURI();
               
               array[i] = location.toURL();
            }
            return new URLClassLoader(array, loader);  
         }
         return loader;
      } finally {
         reader.close();
      }
   }
}
