package org.snapscript.develop.tunnel;

import org.snapscript.agent.ProcessMode;
import org.snapscript.agent.event.BeginEvent;
import org.snapscript.agent.event.ExitEvent;
import org.snapscript.agent.event.ProcessEventAdapter;
import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.develop.ProcessAgentController;

public class ProcessAgentBeginListener extends ProcessEventAdapter {
   
   private final ProcessAgentController controller;
   
   public ProcessAgentBeginListener(ProcessAgentController controller) {
      this.controller = controller;
   }
   
   public void onBegin(ProcessEventChannel channel, BeginEvent event) {
      String process = event.getProcess();
      controller.start(process);
   }

   public void onExit(ProcessEventChannel channel, ExitEvent event) {
      String process = event.getProcess();
      ProcessMode mode = event.getMode();
      
      if(mode.isTerminateRequired()) {
         controller.stop(process);
      }
      if(mode.isDetachRequired()) {
         controller.detach(process);
      }
   }
}