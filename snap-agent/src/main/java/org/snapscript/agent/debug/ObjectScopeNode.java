/*
 * ObjectScopeNode.java December 2016
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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.snapscript.core.index.ModifierConverter;

public class ObjectScopeNode implements ScopeNode {
   
   private final ModifierConverter converter;
   private final ScopeNodeBuilder builder;
   private final List<ScopeNode> nodes;
   private final Object object;
   private final String path;
   private final String name;
   private final int depth;
   
   public ObjectScopeNode(ScopeNodeBuilder builder, Object object, String path, String name, int depth) {
      this.converter = new ModifierConverter();
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
         Class type = object.getClass();
         Map<String, Field> fields = getFields(type);
         Set<String> names = fields.keySet();
         
         if(!names.isEmpty()) {
            for(String name : names) {
               try {
                  Field field = fields.get(name);
                  Object value = field.get(object);
                  int modifiers = converter.convert(field);
                  ScopeNode node = builder.createNode(path + "." + name, name, value, modifiers, depth);
                  
                  if(node != null) {
                     nodes.add(node);
                  }
               } catch(Exception e) {
                  e.printStackTrace();
               }
            }
         }
      }
      return nodes;
   }
   
   private Map<String, Field> getFields(Class type) {
      Map<String, Field> accessors = new HashMap<String, Field>();
      
      while(type != Object.class) {
         Field[] fields = type.getDeclaredFields();
         
         for(int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String name = field.getName();
            int modifiers = field.getModifiers();
            
            if(!Modifier.isStatic(modifiers) || !Modifier.isFinal(modifiers)) {
               field.setAccessible(true);
               accessors.put(name, field);
            }
         }
         type = type.getSuperclass();
      }
      return accessors;
   }
}