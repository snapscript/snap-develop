package org.snapscript.agent.event;

public class StepEvent implements ProcessEvent {
   
   public static final int RUN = 0;
   public static final int STEP_IN = 1;
   public static final int STEP_OVER = 2;
   public static final int STEP_OUT = 3;
   
   private String process;
   private String thread;
   private int type;
   
   public StepEvent(String process, String thread, int type) {
      this.process = process;
      this.thread = thread;
      this.type = type;
   }

   @Override
   public String getProcess() {
      return process;
   }
   
   public String getThread() {
      return thread;
   }
   
   public int getType() {
      return type;
   }

}
