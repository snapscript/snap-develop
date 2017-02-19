/*
 * ValueDataBuilder.java December 2016
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

import org.snapscript.core.Context;
import org.snapscript.core.Type;
import org.snapscript.core.define.Instance;

public class ValueDataBuilder {
   
   private static final int MAX_LENGTH = 1024;
   
   private final ArrayStringBuilder builder;
   private final int limit;
   
   public ValueDataBuilder(Context context) {
      this(context, MAX_LENGTH);
   }
   
   public ValueDataBuilder(Context context, int limit) {
      this.builder = new ArrayStringBuilder(context, limit);
      this.limit = limit;
   }

   public ValueData createNull(String key, Object value, int modifiers, int depth) {
      return new ValueData(key, "", "null", "null", false, modifiers, depth);
   }
   
   public ValueData createArray(String key, Object value, int modifiers, int depth) {
      StringBuilder dimensions = new StringBuilder();
      Class type = value.getClass();
      Class entry = type.getComponentType();
      String text = "";
      
      if(type == byte[].class) {
         text = builder.toString((byte[])value);
      } else if(type == int[].class) { 
         text = builder.toString((int[])value);
      } else if(type == long[].class) {
         text = builder.toString((long[])value);
      } else if(type == double[].class) {
         text = builder.toString((double[])value);
      } else if(type == float[].class) {
         text = builder.toString((float[])value);
      } else if(type == short[].class) {
         text = builder.toString((short[])value);
      } else if(type == char[].class) {
         text = builder.toString((char[])value);
      } else if(type == boolean[].class) {
         text = builder.toString((boolean[])value);
      } else {
         text = builder.toString((Object[])value); 
      }
      while(entry != null) {
         dimensions.append("[]");
         type = entry;
         entry = type.getComponentType();
      }
      String name = type.getSimpleName();
      int length = text.length();
      
      if(length > limit) {
         text = text.substring(0, limit) + "..."; // truncate value
      }
      return new ValueData(key, name + dimensions, "", text, true, modifiers, depth);
   }
   
   public ValueData createObject(String key, Object value, int modifiers, int depth) {
      Class type = value.getClass();
      String name = type.getSimpleName();
      String text = String.valueOf(value);
      int length = text.length();
      
      if(length > limit) {
         text = text.substring(0, limit) + "..."; // truncate value
      }
      return new ValueData(key, name, "", text, true, modifiers, depth);
   }
   
   public ValueData createPrimitive(String key, Object value, int modifiers,int depth) {
      Class type = value.getClass();
      String name = type.getSimpleName();
      String text = String.valueOf(value);
      int length = text.length();
      
      if(length > limit) {
         text = text.substring(0, limit) + "..."; // truncate value
      }
      return new ValueData(key, name, text, text, false, modifiers, depth);
   }
   
   public ValueData createScope(String key, Object value, int modifiers, int depth) {
      Instance instance = (Instance)value;
      Type type = instance.getType();
      String name = type.getName();
      return new ValueData(key, name, "", "", true, modifiers, depth);
   }
}
