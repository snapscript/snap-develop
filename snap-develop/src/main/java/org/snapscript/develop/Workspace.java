/*
 * Workspace.java December 2016
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

package org.snapscript.develop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class Workspace {

   private final File root;
   
   public Workspace(File root){
      this.root = root;
   }
   
   public File create(String name) {
      File file = new File(root, name);
      
      try {
         File directory = file.getParentFile();
         
         if(!directory.exists()) {
            directory.mkdirs();
         }
         return file.getCanonicalFile();
      }catch(Exception e) {
         throw new IllegalStateException("Could not create directory " + file, e);
      }
   }
   
   public File create() {
      try {
         File directory = root.getCanonicalFile();
         
         if(!directory.exists()){
            if(!directory.mkdirs()) {
            throw new IllegalStateException("Could not build work directory " + directory);
            }
            File ignore = new File(directory, ".gitignore");
            OutputStream stream = new FileOutputStream(ignore);
            PrintStream print = new PrintStream(stream);
            print.println("/.temp/");
            print.close();
         }
         return directory;
      }catch(Exception e) {
         throw new IllegalStateException("Could not create directory " + root, e);
      }
   }
}
