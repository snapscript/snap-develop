/*
 * BreakpointsEvent.java December 2016
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

import java.util.Collections;
import java.util.Map;

public class BreakpointsEvent implements ProcessEvent {

   private final Map<String, Map<Integer, Boolean>> breakpoints;
   private final String process;
   
   private BreakpointsEvent(Builder builder) {
      this.breakpoints = Collections.unmodifiableMap(builder.breakpoints);
      this.process = builder.process;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   public Map<String, Map<Integer, Boolean>> getBreakpoints() {
      return breakpoints;
   }
   
   public static class Builder {
      
      private Map<String, Map<Integer, Boolean>> breakpoints;
      private String process;
      
      public Builder(String process){
         this.process = process;
      }

      public Builder withBreakpoints(Map<String, Map<Integer, Boolean>> breakpoints) {
         this.breakpoints = breakpoints;
         return this;
      }

      public Builder withProcess(String process) {
         this.process = process;
         return this;
      }
      
      public BreakpointsEvent build() {
         return new BreakpointsEvent(this);
      }
   }
}