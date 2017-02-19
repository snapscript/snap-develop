/*
 * FaultContextExtractor.java December 2016
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.snapscript.agent.event.FaultEvent;
import org.snapscript.agent.event.FaultEventMarshaller;
import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.core.Context;
import org.snapscript.core.Module;
import org.snapscript.core.Path;
import org.snapscript.core.Scope;
import org.snapscript.core.State;
import org.snapscript.core.error.InternalError;
import org.snapscript.core.error.InternalErrorBuilder;
import org.snapscript.core.stack.ThreadStack;
import org.snapscript.core.trace.Trace;

public class FaultContextExtractor extends TraceAdapter {

   private final ProcessEventChannel channel;
   private final AtomicInteger counter;
   private final ProcessLogger logger;
   private final String process;
   
   public FaultContextExtractor(ProcessEventChannel channel, ProcessLogger logger, String process) {
      this.counter = new AtomicInteger();
      this.channel = channel;
      this.logger = logger;
      this.process = process;
   }

   @Override
   public void error(Scope scope, Trace trace, Exception cause) {      
      if(logger.isDebug()) {
         ScopeVariableTree variables = createVariables(scope);
         String error = createException(scope, cause);
         String thread = Thread.currentThread().getName();
         Path path = trace.getPath();
         String resource = path.getPath();
         int line = trace.getLine();
         
         FaultEvent event = new FaultEvent.Builder(process)
            .withVariables(variables)
            .withCause(error)
            .withLine(line)
            .withResource(resource)
            .withThread(thread)
            .build();
         
         try {
            channel.send(event);
         }catch(Exception e) {
            logger.debug("Could not send fault context", e);
         }
      }
   }
   
   private ScopeVariableTree createVariables(Scope scope) {
      Module module = scope.getModule();
      Context context = module.getContext();
      State state = scope.getState();
      Iterator<String> iterator = state.iterator();
      int change = counter.getAndIncrement();
      
      if(iterator.hasNext() && logger.isDebug()) {
         Set<String> expand = new HashSet<String>();
         ScopeNodeTraverser traverser = new ScopeNodeTraverser(context, scope); 
         
         while(iterator.hasNext()) {
            String name = iterator.next();
            expand.add(name+".*");
         }
         Map<String, Map<String, String>> variables = traverser.expand(expand);
         
         return new ScopeVariableTree.Builder(change)
            .withEvaluation(Collections.EMPTY_MAP)
            .withLocal(variables)
            .build();
      }
      return new ScopeVariableTree.Builder(change)
         .withEvaluation(Collections.EMPTY_MAP)
         .withLocal(Collections.EMPTY_MAP)
         .build(); 
   }
   
   private String createException(Scope scope, Exception cause) {
      Module module = scope.getModule();
      Context context = module.getContext();
      ThreadStack stack = context.getStack();
      
      if(logger.isDebug()) {
         InternalErrorBuilder converter = new InternalErrorBuilder(stack, true);
         StringWriter builder = new StringWriter();
         PrintWriter writer = new PrintWriter(builder);
         
         InternalError error = converter.create(cause);
         Object inner = error.getValue();
         
         if(Throwable.class.isInstance(inner)) {
            Throwable throwable = (Throwable)inner;
         
            throwable.printStackTrace(writer);
            writer.flush();
         } else {
            error.printStackTrace(writer);
            writer.flush();
         }
         
         return builder.toString();
      }
      return "";
   }
}
