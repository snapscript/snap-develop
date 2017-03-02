/*
 * ProjectFile.java December 2016
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

package org.snapscript.develop.http.project;

import java.io.File;

public class ProjectFile {

   private final ProjectFileSystem fileSystem;
   private final String location;
   private final File file;
   private final long time;
   private String text;
   private byte[] data;
   
   public ProjectFile(ProjectFileSystem fileSystem, String location, File file, long time) {
      this.fileSystem = fileSystem;
      this.location = location;
      this.file = file;
      this.time = time;
   }
   
   public File getFile() {
      return file;
   }
   
   public byte[] getByteArray() {
      try {
         if(data == null) {
            data = fileSystem.readAsByteArray(location);
         }
      } catch(Exception e){
         throw new IllegalStateException("Could not encode " + location, e);
      }
      return data;
   }
   
   public String getString() {
      try {
         if(text == null) {
            text = fileSystem.readAsString(location);
         }
      } catch(Exception e){
         throw new IllegalStateException("Could not encode " + location, e);
      }
      return text;
   }
   
   public boolean isStale() {
      if(file != null) {
         return file.lastModified() > time;
      }
      return true;
   }
}
