/*
 * JarBuilder.java December 2016
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

import java.io.ByteArrayOutputStream;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class JarBuilder {

   private final ClassResourceLoader loader;
   
   public JarBuilder(ClassResourceLoader loader) {
      this.loader = loader;
   }

   public byte[] createJar(String mainClass, String... resources) throws Exception {
      Manifest manifest = new Manifest();
      manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
      manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, mainClass);
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      JarOutputStream out = new JarOutputStream(stream, manifest);

      for (String resource : resources) {
         String path = resource;
         
         if(path.startsWith("/")) {
            path = resource.substring(1); // /org/domain/Type.class -> org/domain/Type.class
         }
         JarEntry entry = new JarEntry(path);
         byte[] data = loader.loadClass(resource);
         
         if(data == null) {
            throw new IllegalStateException("Could not fine resource " + resource);
         }
         out.putNextEntry(entry);
         out.write(data);
      }
      out.close();
      stream.close();
      return stream.toByteArray();
   }
}
