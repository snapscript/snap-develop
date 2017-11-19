package org.snapscript.studio.tunnel;

import org.snapscript.studio.agent.ProcessMode;
import org.snapscript.studio.agent.event.BeginEvent;
import org.snapscript.studio.agent.event.ExitEvent;
import org.snapscript.studio.agent.event.ProcessEventAdapter;
import org.snapscript.studio.agent.event.ProcessEventChannel;
import org.snapscript.studio.core.ProcessAgentController;

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