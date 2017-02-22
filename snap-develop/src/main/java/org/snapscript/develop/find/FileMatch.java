/*
 * FileMatch.java December 2016
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

public class FileMatch implements Comparable<FileMatch> {

   private final String resource;
   private final String project;
   private final String text;
   
   public FileMatch(String project, String resource, String text) {
      this.resource = resource;
      this.project = project;
      this.text = text;
   }

   @Override
   public int compareTo(FileMatch other) {
      return resource.compareTo(other.resource);
   }
   
   public String getText(){
      return text;
   }

   public String getProject() {
      return project;
   }
   
   public String getResource() {
      return resource;
   }

}
