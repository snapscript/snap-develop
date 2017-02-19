/*
 * ProcessAgentService.java December 2016
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

package org.snapscript.agent;

import org.snapscript.agent.debug.BreakpointMatcher;
import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.core.Model;
import org.snapscript.core.ResourceManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class ProcessAgentService {
   
   private final Map<String, Map<Integer, Boolean>> breakpoints;
   private final ProcessEventChannel channel;
   private final ProcessResourceExecutor executor;
   private final ProcessContext context;
   
   public ProcessAgentService(ProcessContext context, ProcessEventChannel channel, ProcessResourceExecutor executor, ProcessMode mode, Model model) {
      this.breakpoints = new HashMap<String, Map<Integer, Boolean>>();
      this.executor = executor;
      this.channel = channel;
      this.context = context;
   }

   public String loadScript(String project, String resource) {
      ResourceManager manager = context.getManager();
      String path = ProcessStore.getPath(project, resource);

      return manager.getString(path);
   }
   
   public void createBreakpoint(String resource, int line) {
      Map<Integer, Boolean> lines = breakpoints.get(resource);
      BreakpointMatcher matcher = context.getMatcher();
      
      if(lines == null) {
         lines = new HashMap<Integer, Boolean>();
         breakpoints.put(resource, lines);
      }
      lines.put(line, Boolean.TRUE);
      matcher.update(breakpoints);
   }
   
   public void removeBreakpoint(String resource, int line){
      Map<Integer, Boolean> lines = breakpoints.get(resource);
      BreakpointMatcher matcher = context.getMatcher();
      
      if(lines == null) {
         lines = new HashMap<Integer, Boolean>();
         breakpoints.put(resource, lines);
      }
      lines.put(line, Boolean.FALSE);
      matcher.update(breakpoints);
   }
   
   public void execute(String project, String resource) {
      BreakpointMatcher matcher = context.getMatcher();
      ProcessStore store = context.getStore();
      String actual = context.getProcess();

      matcher.update(breakpoints);
      store.update(project); 
      executor.execute(channel, actual, project, resource);
   }

   public boolean join(long time) {
      CountDownLatch latch = context.getLatch();

      try {
         latch.await();
      }catch(Exception e) {
         return false;
      }
      return true;
   }
}
