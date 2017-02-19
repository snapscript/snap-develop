/*
 * EvaluateEvent.java December 2016
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

package org.snapscript.agent.event;

import java.util.Set;

public class EvaluateEvent implements ProcessEvent {

   private final Set<String> expand;
   private final String expression;
   private final String process;
   private final String thread;
   private final boolean refresh;
   
   private EvaluateEvent(Builder builder) {
      this.expression = builder.expression;
      this.refresh = builder.refresh;
      this.expand = builder.expand;
      this.process = builder.process;
      this.thread = builder.thread;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   public Set<String> getExpand() {
      return expand;
   }
   
   public boolean isRefresh() {
      return refresh;
   }

   public String getExpression() {
      return expression;
   }
   
   public String getThread() {
      return thread;
   }

   public static class Builder {
      
      private Set<String> expand;
      private String expression;
      private String process;
      private String thread;
      private boolean refresh;
      
      public Builder(String process) {
         this.process = process;
      }

      public Builder withExpand(Set<String> expand) {
         this.expand = expand;
         return this;
      }
      
      public Builder withThread(String thread) {
         this.thread = thread;
         return this;
      }

      public Builder withExpression(String expression) {
         this.expression = expression;
         return this;
      }

      public Builder withProcess(String process) {
         this.process = process;
         return this;
      }
      
      public Builder withRefresh(boolean refresh) {
         this.refresh = refresh;
         return this;
      }
      
      public EvaluateEvent build(){
         return new EvaluateEvent(this);
      }
   }
}