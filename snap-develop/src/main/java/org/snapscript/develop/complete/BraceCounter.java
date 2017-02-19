/*
 * BraceCounter.java December 2016
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.snapscript.common.ArrayStack;
import org.snapscript.common.Stack;

public class BraceCounter {
   
   public static final String OPEN_BRACE = "{";
   public static final String CLOSE_BRACE = "}";

   private final AtomicReference<String> current;
   private final Stack<BraceContext> stack;
   private final Map<String, String> global;

   public BraceCounter(){
      this.global = new HashMap<String, String>();
      this.current = new AtomicReference<String>();
      this.stack = new ArrayStack<BraceContext>();
   }
   
   public String getType(){
      if(!stack.isEmpty()) {
         BraceContext context = stack.peek();
         return context.getType();
      }
      return null;
   }
   
   public Map<String, String> getTokens(String prefix) {
      Map<String, String> total = new HashMap<String, String>();
      
      while(!stack.isEmpty()) {
         BraceContext context = stack.pop();
         Map<String, String> tokens = context.getTokens();
         Set<String> names = tokens.keySet();
         
         for(String name : names) {
            if(!total.containsKey(name) && name.startsWith(prefix)) {
               String value = tokens.get(name);
               total.put(name, value);
            }
         }
      }
      Set<String> names = global.keySet();
      
      for(String name : names) {
         if(!total.containsKey(name) && name.startsWith(prefix)) {
            String value = global.get(name);
            total.put(name, value);
         }
      }
      return total;
   }
   
   public void setType(String type){
      current.set(type);
   }
   
   public void addToken(String name, String type) {
      if(!stack.isEmpty()) {
         BraceContext context = stack.peek();
         Map<String, String> tokens = context.getTokens();
         tokens.put(name, type);
      } else{
         global.put(name, type);
      }
   }
    
   public void setBrace(String brace) {
      String type = current.get();
      
      if(brace.equals(OPEN_BRACE)) {
         BraceContext context = new BraceContext(type);
         stack.push(context);
      }
      if(brace.equals(CLOSE_BRACE)){
         stack.pop();
      }
   }
   
   private static class BraceContext {
      
      private final Map<String, String> tokens;
      private final String type;
      
      public BraceContext(String type) {
         this.tokens = new HashMap<String, String>();
         this.type = type;
      }
      
      public Map<String, String> getTokens() {
         return tokens;
      }
      
      public String getType() {
         return type;
      }
   }
}
