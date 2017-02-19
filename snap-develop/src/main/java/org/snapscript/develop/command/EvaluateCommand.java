/*
 * EvaluateCommand.java December 2016
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

package org.snapscript.develop.command;

import java.util.Set;

public class EvaluateCommand implements Command {

   private Set<String> expand;
   private String expression;
   private String thread;
   private boolean refresh;
   
   public EvaluateCommand() {
      super();
   }
   
   public EvaluateCommand(String thread, String expression, boolean refresh, Set<String> expand) {
      this.expression = expression;
      this.thread = thread;
      this.refresh = refresh;
      this.expand = expand;
   }

   public boolean isRefresh() {
      return refresh;
   }

   public void setRefresh(boolean refresh) {
      this.refresh = refresh;
   }

   public Set<String> getExpand() {
      return expand;
   }

   public void setExpand(Set<String> expand) {
      this.expand = expand;
   }

   public String getExpression() {
      return expression;
   }

   public void setExpression(String expression) {
      this.expression = expression;
   }

   public String getThread() {
      return thread;
   }

   public void setThread(String thread) {
      this.thread = thread;
   }
}
