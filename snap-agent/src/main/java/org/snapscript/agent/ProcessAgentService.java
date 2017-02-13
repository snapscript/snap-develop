package org.snapscript.agent;

import java.util.HashMap;
import java.util.Map;

import org.snapscript.agent.debug.BreakpointMatcher;
import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.core.Model;

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
}
