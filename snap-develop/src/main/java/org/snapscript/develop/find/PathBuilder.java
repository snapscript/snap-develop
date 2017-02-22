/*
 * PathBuilder.java December 2016
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

package org.snapscript.develop.find;

import java.io.File;

public class PathBuilder {
   
   private final String root;
   
   public PathBuilder(String root) {
      this.root = root;
   }

   public String buildPath(File file) {
      try {
         String filePath = file.getCanonicalPath();
         String relativePath = filePath.replace(root, "");
         String resourcePath = relativePath.replace(File.separatorChar, '/');
         
         if(!resourcePath.startsWith("/")) {
            resourcePath = "/" + resourcePath;
         }
         return resourcePath;
      }catch(Exception e) {
         throw new IllegalArgumentException("Could not build path from " + file, e);
      }
   }
}
