
package org.snapscript.develop.command;

public class StepCommand implements Command {

   private static enum StepType {
      RUN,
      STEP_IN,
      STEP_OVER,
      STEP_OUT;
   }
   
   private String thread;
   private StepType type;
   
   public StepCommand(String thread, StepType type) {
      this.thread = thread;
      this.type = type;
   }
   
   public String getThread() {
      return thread;
   }
   
   public boolean isRun() {
      return type == StepType.RUN;
   }
   
   public boolean isStepIn() {
      return type == StepType.STEP_IN;
   }
   
   public boolean isStepOver() {
      return type == StepType.STEP_OVER;
   }
   
   public boolean isStepOut() {
      return type == StepType.STEP_OUT;
   }
}
