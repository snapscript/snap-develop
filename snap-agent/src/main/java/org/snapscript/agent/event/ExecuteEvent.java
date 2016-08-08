package org.snapscript.agent.event;

import java.util.Map;

public class ExecuteEvent implements ProcessEvent {

   private Map<String, Map<Integer, Boolean>> breakpoints;
   private String project;
   private String resource;
   private String process;
   
   public ExecuteEvent(String process, String project, String resource, Map<String, Map<Integer, Boolean>> breakpoints) {
      this.breakpoints = breakpoints;
      this.project = project;
      this.resource = resource;
      this.process = process;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   public Map<String, Map<Integer, Boolean>> getBreakpoints() {
      return breakpoints;
   }
   
   public String getResource() {
      return resource;
   }
   
   public String getProject() {
      return project;
   }
}
