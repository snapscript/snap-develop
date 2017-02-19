/*
 * TypeNodeFinder.java December 2016
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

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.develop.configuration.ConfigurationClassLoader;

public class TypeNodeFinder {

   private final ResourceTypeLoader compiler;
   
   public TypeNodeFinder(ConfigurationClassLoader loader, ProcessLogger logger) {
      this.compiler = new ResourceTypeLoader(loader, logger);
   }
   
   public Map<String, TypeNode> parse(File root, String project, String resource, String source) {
      Map<String, TypeNode> types = new HashMap<String, TypeNode>();
      
      try {
         Map<String, TypeNode> nodes = compiler.compileSource(root, resource, source);
         Set<String> names = nodes.keySet();
         
         for(String name : names) {
            if(!name.contains(".")) {
               TypeNode node = nodes.get(name);
               String path = node.getResource();
               
               if(path.equals(resource)) {
                  types.put(name, node);
               }
            }
         }
      }catch(Exception cause) {
         return Collections.emptyMap();
      }
      return types;
   }
}
