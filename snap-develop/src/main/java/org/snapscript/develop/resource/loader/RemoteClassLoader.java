/*
 * RemoteClassLoader.java December 2016
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

import java.net.URL;
import java.net.URLClassLoader;

public class RemoteClassLoader extends URLClassLoader {

   private final String prefix;
   
   public RemoteClassLoader(URL[] path, ClassLoader parent, String prefix) {
      super(path, parent);
      this.prefix = prefix;
   }
   
   @Override
   public Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
      ClassLoader loader = getParent();
      
      if(name.startsWith(prefix)) { // it should be remote
         return super.loadClass(name, resolve);
      }
      return loader.loadClass(name);

   }
}
