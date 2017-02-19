/*
 * Completion.java December 2016
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

package org.snapscript.develop.complete;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.snapscript.develop.common.TypeNode;

public class Completion {

   private final Map<String, TypeNode> types;
   private final Map<String, String> tokens;
   private final String resource;
   private final String complete;
   private final String source;
   private final String prefix;
   private final File root;
   private final int line;
   
   public Completion(File root, String source, String resource, String prefix, String complete, int line) {
      this.types = new HashMap<String, TypeNode>();
      this.tokens = new TreeMap<String, String>();
      this.resource = resource;
      this.complete = complete;
      this.source = source;
      this.prefix = prefix;
      this.line = line;
      this.root = root;
   }
   
   public Map<String, TypeNode> getTypes() {
      return types;
   }
   
   public Map<String, String> getTokens() {
      return tokens;
   }
   
   public File getRoot() {
      return root;
   }

   public String getResource() {
      return resource;
   }

   public String getComplete() {
      return complete;
   }

   public String getSource() {
      return source;
   }

   public String getPrefix() {
      return prefix;
   }

   public int getLine() {
      return line;
   } 
}
