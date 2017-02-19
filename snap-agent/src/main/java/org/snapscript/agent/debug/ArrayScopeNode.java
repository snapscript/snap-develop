/*
 * ArrayScopeNode.java December 2016
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

package org.snapscript.agent.debug;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ArrayScopeNode implements ScopeNode {
   
   private final ScopeNodeBuilder builder;
   private final List<ScopeNode> nodes;
   private final Object object;
   private final String name;
   private final String path;
   private final int depth;
   
   public ArrayScopeNode(ScopeNodeBuilder builder, Object object, String path, String name, int depth) {
      this.nodes = new ArrayList<ScopeNode>();
      this.builder = builder;
      this.object = object;
      this.depth = depth;
      this.path = path;
      this.name = name;
   }
   
   @Override
   public int getDepth() {
      return depth;
   }
   
   @Override
   public String getName() {
      return name;
   }
   
   @Override
   public String getPath() {
      return path;
   }

   @Override
   public List<ScopeNode> getNodes() {
      if(nodes.isEmpty()) {
         int length = Array.getLength(object);
         
         if(length > 0) {
            for(int i = 0; i < length; i++) {
               try {
                  Object value = Array.get(object, i);
                  
                  if(value != null) {
                     ScopeNode node = builder.createNode(path + ".[" + i + "]", "[" + i + "]", value, 0, depth);
                     
                     if(node != null) {
                        nodes.add(node);
                     }
                  }
               } catch(Exception e) {
                  e.printStackTrace();
               }
            }
         }
      }
      return nodes;
   }
}