/*
 * ScopeNodeBuilder.java December 2016
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

import java.lang.reflect.Proxy;
import java.util.Map;

import org.snapscript.core.Context;
import org.snapscript.core.PrimitivePromoter;
import org.snapscript.core.convert.ProxyWrapper;
import org.snapscript.core.define.Instance;

public class ScopeNodeBuilder {
   
   private final Map<String, Map<String, String>> variables;
   private final PrimitivePromoter promoter;
   private final ScopeNodeChecker checker;
   private final ValueDataBuilder builder;
   private final Context context;
   
   public ScopeNodeBuilder(Map<String, Map<String, String>> variables, Context context) {
      this.builder = new ValueDataBuilder(context);
      this.promoter = new PrimitivePromoter();
      this.checker = new ScopeNodeChecker();
      this.variables = variables;
      this.context = context;
   }

   public ScopeNode createNode(String path, String name, Object object, int modifiers, int depth) {
      if(object != null) {
         ProxyWrapper wrapper = context.getWrapper();
         
         if(object instanceof Proxy) {
            object = wrapper.fromProxy(object);
         }
         if(object instanceof Instance) {
            Instance instance = (Instance)object;
            ValueData data = builder.createScope(name, instance, modifiers, depth);
            Map<String, String> map = data.getData();
            
            variables.put(path, map); // put the type rather than value
            return new InstanceScopeNode(this, instance, path, name, depth + 1);
         }
         Class actual = object.getClass();
         Class type = promoter.promote(actual);
         
         if(!checker.isPrimitive(type)) { 
            if(type.isArray()) {
               ValueData data = builder.createArray(name, object, modifiers, depth);
               Map<String, String> map = data.getData();
               
               variables.put(path, map); // type rather than value
               return new ArrayScopeNode(this, object, path, name, depth + 1);
            } else {
               ValueData data = builder.createObject(name, object, modifiers, depth);
               Map<String, String> map = data.getData();
               
               variables.put(path, map); // type rather than value
               return new ObjectScopeNode(this, object, path, name, depth + 1);
            }
         } else {
            ValueData data = builder.createPrimitive(name, object, modifiers, depth);
            Map<String, String> map = data.getData();
            
            variables.put(path, map);
         }
      } else {
         ValueData data = builder.createNull(name, object, modifiers, depth);
         Map<String, String> map = data.getData();
         
         variables.put(path, map);
      }
      return null;
   }

}