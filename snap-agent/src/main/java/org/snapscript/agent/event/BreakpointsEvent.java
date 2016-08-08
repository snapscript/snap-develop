package org.snapscript.agent.event;

import java.util.Map;

public class BreakpointsEvent implements ProcessEvent {

   private Map<String, Map<Integer, Boolean>> breakpoints;
   private String process;
   
   public BreakpointsEvent(String process, Map<String, Map<Integer, Boolean>> breakpoints) {
      this.breakpoints = breakpoints;
      this.process = process;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   public Map<String, Map<Integer, Boolean>> getBreakpoints() {
      return breakpoints;
   }
}