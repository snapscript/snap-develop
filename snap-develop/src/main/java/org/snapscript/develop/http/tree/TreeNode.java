/*
 * TreeNode.java December 2016
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TreeNode {

   private final Comparator<File> comparator;
   private final File file; 
   private final String path; 
   private final String indent;
   private final String prefix;
   private final String id; 
   private final int depth; 
   private final boolean expand;
   private final boolean root;
   
   private TreeNode(Builder builder) {
      this.comparator = new TreeFileComparator();
      this.file = builder.file;
      this.path = builder.path;
      this.indent = builder.indent;
      this.prefix = builder.prefix;
      this.id = builder.id;
      this.depth = builder.depth;
      this.expand = builder.expand;
      this.root = builder.root;
   }
   
   public List<File> getFiles() {
      File[] list = file.listFiles();
      
      if(list != null) {
         Arrays.sort(list, comparator);
         return Arrays.asList(list);
      }
      return Collections.emptyList();
   }
   
   public File getFile() {
      return file;
   }
   
   public String getName() {
      return file.getName();
   }
   
   public String getPath() {
      return path;
   }

   public String getIndent() {
      return indent;
   }

   public String getPrefix() {
      return prefix;
   }

   public String getId() {
      return id;
   }

   public int getDepth() {
      return depth;
   }
   
   public boolean isDirectory(){
      return file.isDirectory();
   }

   public boolean isExpand() {
      return expand;
   }
   
   public boolean isRoot() {
      return root;
   }

   public static class Builder {
      
      private File file; 
      private String path; 
      private String indent;
      private String prefix;
      private String id; 
      private int depth; 
      private boolean expand;
      private boolean root;
      
      public Builder(File file) {
         this.file = file;
      }

      public Builder withFile(File file) {
         this.file = file;
         return this;
      }

      public Builder withPath(String path) {
         this.path = path;
         return this;
      }

      public Builder withIndent(String indent) {
         this.indent = indent;
         return this;
      }

      public Builder withPrefix(String prefix) {
         this.prefix = prefix;
         return this;
      }

      public Builder withId(String id) {
         this.id = id;
         return this;
      }

      public Builder withDepth(int depth) {
         this.depth = depth;
         return this;
      }

      public Builder withExpand(boolean expand) {
         this.expand = expand;
         return this;
      }
      
      public Builder withRoot(boolean root) {
         this.root = root;
         return this;
      }
      
      public TreeNode build() {
         return new TreeNode(this);
      }
      
   }
}

