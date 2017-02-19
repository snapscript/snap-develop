/*
 * Problem.java December 2016
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

package org.snapscript.develop.common;

public class Problem {

   private final String description;
   private final String resource;
   private final String project;
   private final int line;
   
   public Problem(String project, String resource, String description, int line) {
      this.description = description;
      this.resource = resource;
      this.project = project;
      this.line = line;
   }
   
   public String getProject() {
      return project;
   }
   
   public String getDescription() {
      return description;
   }
   
   public String getResource() {
      return resource;
   }
   
   public int getLine() {
      return line;
   }
}
