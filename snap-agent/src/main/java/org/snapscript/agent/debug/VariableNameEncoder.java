/*
 * VariableNameEncoder.java December 2016
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

public class VariableNameEncoder {

   private static final String DEFAULT_TOKEN = "__DOT__";
   
   private final String token;
   
   public VariableNameEncoder() {
      this(DEFAULT_TOKEN);
   }
   
   public VariableNameEncoder(String token) {
      this.token = token;
   }
   
   public String encode(String name) {
      if(name != null) {
         return name.replace(".", token);
      }
      return name;
   }
   
   public String decode(String name) {
      if(name != null) {
         return name.replace(token, ".");
      }
      return name;
   }
}
