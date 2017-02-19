/*
 * TypeNodeReference.java December 2016
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

public class TypeNodeReference {

   public static final String CLASS = "class";
   public static final String MODULE = "module";
   
   private final String name;
   private final String resource;
   private final String type;
   
   public TypeNodeReference(String name, String resource, String type) {
      this.name = name;
      this.resource = resource;
      this.type = type;
   }

   public String getName() {
      return name;
   }

   public String getResource() {
      return resource;
   }

   public String getType() {
      return type;
   }
}
