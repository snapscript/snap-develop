/*
 * ExpressionProcessor.java December 2016
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.snapscript.core.Context;
import org.snapscript.core.ExpressionEvaluator;
import org.snapscript.core.Scope;

public class ExpressionProcessor {
   
   private static final Object NULL_VALUE = new Object();

   private final Map<String, Object> results; // holds only one expression
   private final Context context;
   private final Scope scope;
   
   public ExpressionProcessor(Context context, Scope scope) {
      this.results = new ConcurrentHashMap<String, Object>();
      this.context = context;
      this.scope = scope;
   }
   
   public Object evaluate(String expression) {
      return evaluate(expression, false);
   }
   
   public Object evaluate(String expression, boolean refresh) {
      if(refresh) {
         results.clear();
      }
      if(!accept(expression)) {
         results.clear();
         return null;
      }
      if(!results.containsKey(expression)) { // only evaluate once
         Object result = execute(expression);
         
         results.clear(); // clear all expression when changed
         results.put(expression, result); // represents null
      }
      Object result = results.get(expression);
      
      if(result != NULL_VALUE) {
         return result;
      }
      return null;
   }
   
   private Object execute(String expression) {
      try {
         ExpressionEvaluator evaluator = context.getEvaluator();
         Object result =  evaluator.evaluate(scope, expression);

         if(result == null) {
            return NULL_VALUE; // this is a special 'null' value
         } 
         return result;
      } catch(Exception cause) {
         cause.printStackTrace();
         return cause;
      }
   }
   
   private boolean accept(String expression) {
      if(expression != null) {
         String token = expression.trim();
         int length = token.length();
         
         if(length > 0) {
            return true; 
         }
      }
      return false;        
   }
}
