package org.snapscript.studio.tunnel;

import org.snapscript.agent.ProcessMode;
import org.snapscript.agent.event.BeginEvent;
import org.snapscript.agent.event.ExitEvent;
import org.snapscript.agent.event.ProcessEventAdapter;
import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.studio.ProcessAgentController;

public class ProcessAgentBeginListener extends ProcessEventAdapter {
   
   private final ProcessAgentController controller;
   
   public ProcessAgentBeginListener(ProcessAgentController controller) {
      this.controller = controller;
   }
   
   @Override
   public void onBegin(ProcessEventChannel channel, BeginEvent event) {
      String process = event.getProcess();
      controller.start(process);
   }

   @Override
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