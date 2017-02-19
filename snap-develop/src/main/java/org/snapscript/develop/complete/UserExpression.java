/*
 * UserExpression.java December 2016
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

import java.util.Set;

import org.snapscript.develop.common.TypeNode;

public class UserExpression {
   
   private final SourceContext context;
   private final TypeNode constraint;
   private final Set<String> tokens;
   private final String complete;
   
   public UserExpression(String complete, TypeNode constraint, SourceContext context, Set<String> tokens) {
      this.constraint = constraint;
      this.context = context;
      this.complete = complete;
      this.tokens = tokens;
   }
   
   public SourceContext getContext(){
      return context;
   }
   
   public TypeNode getConstraint() {
      return constraint;
   }
   
   public Set<String> getTypes() {
      return tokens;
   }
   
   public String getExpression() {
      return complete;
   }
   
   @Override
   public String toString() {
      return String.format("%s -> %s", complete, constraint);
   }

}
