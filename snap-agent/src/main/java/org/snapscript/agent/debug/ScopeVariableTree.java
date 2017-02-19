/*
 * ScopeVariableTree.java December 2016
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

import java.util.Collections;
import java.util.Map;

public class ScopeVariableTree {
   
   public static final ScopeVariableTree EMPTY = new ScopeVariableTree.Builder(-1)
      .withEvaluation(Collections.EMPTY_MAP)
      .withLocal(Collections.EMPTY_MAP)
      .build();

   private final Map<String, Map<String, String>> evaluation;
   private final Map<String, Map<String, String>> local;
   private final int change;
   
   private ScopeVariableTree(Builder builder) {
      this.evaluation = Collections.unmodifiableMap(builder.evaluation);
      this.local = Collections.unmodifiableMap(builder.local);
      this.change = builder.change;
   }
   
   public Map<String, Map<String, String>> getLocal() {
      return local;
   }
   
   public Map<String, Map<String, String>> getEvaluation() {
      return evaluation;
   }
   
   public int getChange() {
      return change;
   }
   
   public static class Builder {
      
      private Map<String, Map<String, String>> evaluation;
      private Map<String, Map<String, String>> local;
      private int change;
      
      public Builder(int change){
         this.change = change;
      }

      public Builder withEvaluation(Map<String, Map<String, String>> evaluation) {
         this.evaluation = evaluation;
         return this;
      }

      public Builder withLocal(Map<String, Map<String, String>> local) {
         this.local = local;
         return this;
      }

      public Builder withChange(int change) {
         this.change = change;
         return this;
      }
      
      public ScopeVariableTree build() {
         return new ScopeVariableTree(this);
      }
   }
}
