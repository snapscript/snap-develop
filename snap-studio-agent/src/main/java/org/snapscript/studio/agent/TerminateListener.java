package org.snapscript.studio.agent;

public class TerminateListener implements Runnable {
   
   private final RunMode mode;
   
   public TerminateListener(RunMode mode) {
      this.mode = mode;
   }

   @Override
   public void run() {
      if(mode.isTerminateRequired()) {
         TerminateHandler.terminate("Connection checker timeout elapsed");
      }
   }

}
