/*
 * TreeContext.java December 2016
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

package org.snapscript.develop.http.tree;

import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class TreeContext implements TreeFolderExpander {
   
   private final Set<String> expand;
   private final String project;
   private final File root;
   
   public TreeContext(File root, String project) {
      this.expand = new CopyOnWriteArraySet<String>();
      this.project = project;
      this.root = root;
   }
   
   public File getRoot() {
      return root;
   }
   
   public String getProject() {
      return project;
   }
   
   public Set<String> getExpandFolders() {
      return Collections.unmodifiableSet(expand);
   }
   
   public TreeContext folderExpand(String path) {
      String result = TreePathFormatter.formatPath(project, path);
      expand.add(result);
      return this;
   }
   
   public TreeContext folderCollapse(String path) {
      String result = TreePathFormatter.formatPath(project, path);
      expand.remove(result);
      return this;
   }

   @Override
   public boolean expand(String path) {
      return path != null && expand.contains(path);
   }
   
}
