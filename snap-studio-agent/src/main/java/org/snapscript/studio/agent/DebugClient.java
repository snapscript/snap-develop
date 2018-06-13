package org.snapscript.studio.agent;

import java.util.HashMap;
import java.util.Map;

import org.snapscript.core.ResourceManager;
import org.snapscript.studio.agent.debug.BreakpointMatcher;
import org.snapscript.studio.agent.event.ExitEvent;
import org.snapscript.studio.agent.event.ProcessEventChannel;
import org.snapscript.studio.agent.task.ProcessExecutor;

public class DebugClient {
   
   private final Map<String, Map<Integer, Boolean>> breakpoints;
   private final ProcessEventChannel client;
   private final ProcessExecutor executor;
   private final DebugContext context;
   private final String process;
   
   public DebugClient(DebugContext context, ProcessEventChannel client, ProcessExecutor executor, String process) {
      this.breakpoints = new HashMap<String, Map<Integer, Boolean>>();
      this.executor = executor;
      this.client = client;
      this.context = context;
      this.process = process;
   }

   public String loadScript(String project, String resource) {
      ResourceManager manager = context.getManager();
      String path = RemoteProjectStore.getPath(project, resource);

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
   
   public void beginExecute(String project, String resource, String dependencies, boolean debug) {
      BreakpointMatcher matcher = context.getMatcher();
      ProjectStore store = context.getStore();

      matcher.update(breakpoints);
      store.update(project); 
      executor.beginExecute(client, project, resource, dependencies, debug);
   }
   
   public void attachProcess(String project, String resource) {
      BreakpointMatcher matcher = context.getMatcher();
      ProjectStore store = context.getStore();

      matcher.update(breakpoints);
      store.update(project); 
      executor.attachProcess(client, project, resource);
      matcher.suspend();
   }

   public boolean waitUntilFinish(long time) {
      ExecuteLatch latch = context.getLatch();

      try {
         latch.wait(ExecuteStatus.FINISHED, time);
      }catch(Exception e) {
         return false;
      }
      return true;
   }
   
   public boolean detachClient() {
      ExitEvent event = new ExitEvent.Builder(process)
         .withDuration(0)
         .withMode(context.getMode())
         .build();   
      
      try {      
         client.send(event);
      }catch(Exception e) {
         return false;
      }         
      try {      
         client.close("Client detach");
      }catch(Exception e) {
         return false;
      } 
      return true;
   }
}