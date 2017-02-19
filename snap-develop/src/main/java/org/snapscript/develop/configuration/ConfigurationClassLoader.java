/*
 * ConfigurationClassLoader.java December 2016
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

package org.snapscript.develop.configuration;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ConfigurationClassLoader {
   
   private final AtomicReference<ClassLoader> reference;
   private final ConfigurationReader reader;
   
   public ConfigurationClassLoader(ConfigurationReader reader) {
      this.reference = new AtomicReference<ClassLoader>();
      this.reader = reader;
   }
   
   public Class loadClass(String name) {
      ClassLoader loader = getClassLoader();
      
      try {
         return loader.loadClass(name);
      } catch(Exception e) {
         throw new IllegalArgumentException("Could not find class " + name, e);
      }
   }
   
   public ClassLoader getClassLoader() {
      ClassLoader loader = reference.get();
      
      if(loader == null) {
         loader = getConfigurationClassLoader();
         reference.set(loader);
      }
      return loader;
   }

   private ClassLoader getConfigurationClassLoader() {
      Configuration data = reader.load();
      
      try {
         if(data != null) {
            List<File> dependencies = data.getDependencies();
            List<URL> locations = new ArrayList<URL>();
            URL[] array = new URL[]{};
            
            for(File dependency : dependencies) {
               URL location = dependency.toURI().toURL();
               locations.add(location);
            }
            return new URLClassLoader(
                  locations.toArray(array),
                  ConfigurationClassLoader.class.getClassLoader());
         }
      } catch(Exception e) {
         throw new IllegalStateException("Could not load configuration", e);
      }
      return ConfigurationClassLoader.class.getClassLoader();
   }

}
