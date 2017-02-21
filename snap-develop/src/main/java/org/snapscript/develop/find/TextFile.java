/*
 * TextFile.java December 2016
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

public class TextFile {
   
   private final File file;
   private final String project;
   private final String path;
   
   public TextFile(File file, String project, String path) {
      this.project = project;
      this.file = file;
      this.path = path;
   }
   
   @Override
   public boolean equals(Object value) {
      if(value instanceof TextFile) {
         return equals((TextFile)value);
      }
      return false;
   }
   
   public boolean equals(TextFile value) {
      return value.file.equals(file);
   }
   
   @Override
   public int hashCode() {
      return file.hashCode();
   }

   public File getFile() {
      return file;
   }
   
   public String getProject(){
      return project;
   }

   public String getPath() {
      return path;
   }
   
   @Override
   public String toString(){
      return file.toString();
   }
}
