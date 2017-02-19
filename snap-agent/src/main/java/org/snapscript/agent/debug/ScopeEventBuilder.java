/*
 * ScopeEventBuilder.java December 2016
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

import static org.snapscript.agent.event.ScopeEvent.RUNNING;
import static org.snapscript.agent.event.ScopeEvent.SUSPENDED;

import org.snapscript.agent.event.ScopeEvent;
import org.snapscript.core.trace.TraceType;

public class ScopeEventBuilder {

   private final ScopeVariableTree blank;
   private final ScopeExtractor extractor;
   private final TraceType type;
   private final String process;
   private final String resource;
   private final String thread;
   private final String stack;
   private final int line;
   private final int depth;
   private final int count;
   
   public ScopeEventBuilder(ScopeExtractor extractor, TraceType type, String process, String thread, String stack, String resource, int line, int depth, int count) {
      this.blank = ScopeVariableTree.EMPTY;
      this.extractor = extractor;
      this.process = process;
      this.thread = thread;
      this.resource = resource;
      this.stack = stack;
      this.line = line;
      this.depth = depth;
      this.count = count;
      this.type = type;
   }
   
   public ScopeEvent suspendEvent() {
      ScopeVariableTree variables = extractor.build();
      String name = type.name();
 
      return new ScopeEvent.Builder(process)
         .withVariables(variables)
         .withThread(thread)
         .withStack(stack)
         .withInstruction(name)
         .withStatus(SUSPENDED)
         .withResource(resource)
         .withLine(line)
         .withDepth(depth)
         .withKey(count)
         .build();
   }
   
   public ScopeEvent resumeEvent() {
      String name = type.name();

      return new ScopeEvent.Builder(process)
         .withVariables(blank)
         .withThread(thread)
         .withStack(stack)
         .withInstruction(name)
         .withStatus(RUNNING)
         .withResource(resource)
         .withLine(line)
         .withDepth(depth)
         .withKey(count)
         .build();
   }
}
