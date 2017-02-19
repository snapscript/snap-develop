/*
 * SaveCommand.java December 2016
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

package org.snapscript.develop.command;

public class SaveCommand implements Command {

   private String resource;
   private String project;
   private String source;
   private boolean directory;
   private boolean create;
   
   public SaveCommand() {
      super();
   }
   
   public SaveCommand(String project, String resource, String source, boolean directory, boolean create) {
      this.project = project;
      this.resource = resource;
      this.source = source;
      this.directory = directory;
      this.create = create;
   }

   public String getResource() {
      return resource;
   }

   public void setResource(String resource) {
      this.resource = resource;
   }

   public String getProject() {
      return project;
   }

   public void setProject(String project) {
      this.project = project;
   }

   public String getSource() {
      return source;
   }

   public void setSource(String source) {
      this.source = source;
   }

   public boolean isCreate() {
      return create;
   }

   public void setCreate(boolean create) {
      this.create = create;
   }

   public boolean isDirectory() {
      return directory;
   }

   public void setDirectory(boolean directory) {
      this.directory = directory;
   }

}
