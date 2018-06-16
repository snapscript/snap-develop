package org.snapscript.studio.agent.core;

import org.snapscript.studio.agent.ProcessMode;

public class TerminateListener implements Runnable {
   
   private final ProcessMode mode;
   
   public TerminateListener(ProcessMode mode) {
      this.mode = mode;
   }

   @Override
   public void run() {
      if(mode.isTerminateRequired()) {
         TerminateHandler.terminate("Connection checker timeout elapsed");
      }
   }

}
