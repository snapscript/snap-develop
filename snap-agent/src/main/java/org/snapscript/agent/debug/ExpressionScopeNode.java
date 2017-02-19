/*
 * ExpressionScopeNode.java December 2016
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExpressionScopeNode implements ScopeNode {
   
   private final ExpressionProcessor processor;
   private final VariableNameEncoder encoder;
   private final ScopeNodeBuilder builder;
   private final String expression;
   private final boolean refresh;
   
   public ExpressionScopeNode(ScopeNodeBuilder builder, ExpressionProcessor processor, VariableNameEncoder encoder, String expression, boolean refresh) {
      this.expression = expression;
      this.processor = processor;
      this.refresh = refresh;
      this.encoder = encoder;
      this.builder = builder;
   }
   
   @Override
   public int getDepth() {
      return 0;
   }
   
   @Override
   public String getName() {
      return "";
   }
   
   @Override
   public String getPath() {
      return "";
   }

   @Override
   public List<ScopeNode> getNodes() {
      Object object = processor.evaluate(expression, refresh);
      
      if(expression != null) {
         String token = expression.trim();
         int length = token.length();
         
         if(length > 0) { // make sure something is evaluated
            String path = encoder.encode(expression);
            ScopeNode node = builder.createNode(path, expression, object, 0, 0);
         
            if(node != null) {
               return Collections.singletonList(node);
            }
         }
      }
      return Collections.emptyList();
   }
}