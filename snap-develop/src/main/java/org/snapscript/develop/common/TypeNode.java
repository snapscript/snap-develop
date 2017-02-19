/*
 * TypeNode.java December 2016
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.snapscript.core.Module;
import org.snapscript.core.Type;
import org.snapscript.core.define.SuperExtractor;
import org.snapscript.core.function.Function;
import org.snapscript.core.property.Property;

public class TypeNode {

   private final Object value;
   private final String name;
   
   public TypeNode(Type value, String name) {
      this.value = value;
      this.name = name;
   }
   
   public TypeNode(Module value, String name) {
      this.value = value;
      this.name = name;
   }
   
   public String getName() {
      return name;
   }
   
   public Class getType() {
      if(Type.class.isInstance(value)) {
         return ((Type)value).getType();
      }
      return null;
   }
   
   public List<Function> getFunctions(){
      if(Module.class.isInstance(value)) {
         return ((Module)value).getFunctions();
      }
      if(Type.class.isInstance(value)) {
         List<Function> total = new ArrayList<Function>();
         SuperExtractor extractor = new SuperExtractor();
         Type type = (Type)value;
         
         while(type != null) {
            List<Function> functions = type.getFunctions();
            total.addAll(functions);
            type = extractor.extractor(type);
         }
         return total;
      }
      return Collections.emptyList();
   }
   
   public List<Property> getProperties(){
      if(Type.class.isInstance(value)) {
         List<Property> total = new ArrayList<Property>();
         SuperExtractor extractor = new SuperExtractor();
         Type type = (Type)value;
         
         while(type != null) {
            List<Property> functions = type.getProperties();
            total.addAll(functions);
            type = extractor.extractor(type);
         }
         return total;
      }
      return Collections.emptyList();
   }
   
   public String getModule() {
      if(Module.class.isInstance(value)) {
         return ((Module)value).getName();
      }
      if(Type.class.isInstance(value)) {
         return ((Type)value).getModule().getName();
      }
      return null;
   }
   
   public String getResource() {
      if(Module.class.isInstance(value)) {
         return ((Module)value).getPath().getPath();
      }
      if(Type.class.isInstance(value)) {
         return ((Type)value).getModule().getPath().getPath();
      }
      return null;
   }
   
   public boolean isModule(){
      return Module.class.isInstance(value);
   }
   
   public boolean isType() {
      return Type.class.isInstance(value);
   }
   
   @Override
   public String toString() {
      return name;
   }
}
