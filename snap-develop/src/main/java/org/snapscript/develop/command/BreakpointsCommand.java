
package org.snapscript.develop.command;

import java.util.HashMap;
import java.util.Map;

public class BreakpointsCommand implements Command {

   protected Map<String, Map<Integer, Boolean>> breakpoints;
   protected String project;
   
   public BreakpointsCommand() {
      this.breakpoints = new HashMap<String, Map<Integer, Boolean>>();
   }
   
   public BreakpointsCommand(String project, Map<String, Map<Integer, Boolean>> breakpoints) {
      this.breakpoints = breakpoints;
      this.project = project;
   }

   public Map<String, Map<Integer, Boolean>> getBreakpoints() {
      return breakpoints;
   }

   public void setBreakpoints(Map<String, Map<Integer, Boolean>> breakpoints) {
      this.breakpoints = breakpoints;
   }

   public String getProject() {
      return project;
   }

   public void setProject(String project) {
      this.project = project;
   }
}
