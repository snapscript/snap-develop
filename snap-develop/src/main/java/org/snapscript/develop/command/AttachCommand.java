
package org.snapscript.develop.command;

import java.util.HashMap;
import java.util.Map;

public class AttachCommand extends BreakpointsCommand {

   private String process;
   private boolean focus;
   
   public AttachCommand() {
      this.breakpoints = new HashMap<String, Map<Integer, Boolean>>();
   }
   
   public AttachCommand(String process, String project, boolean focus, Map<String, Map<Integer, Boolean>> breakpoints) {
      super(project, breakpoints);
      this.process = process;
      this.focus = focus;
   }

   public String getProcess() {
      return process;
   }

   public void setProcess(String process) {
      this.process = process;
   }

   public boolean isFocus() {
      return focus;
   }

   public void setFocus(boolean focus) {
      this.focus = focus;
   }
   
   
}