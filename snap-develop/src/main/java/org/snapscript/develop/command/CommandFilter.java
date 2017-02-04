package org.snapscript.develop.command;

import java.util.concurrent.atomic.AtomicReference;

import org.snapscript.agent.event.ProcessEvent;
import org.snapscript.agent.event.ProcessEventFilter;

public class CommandFilter implements ProcessEventFilter {

   private final AtomicReference<String> attachment;
   
   public CommandFilter() {
      this.attachment = new AtomicReference<String>();
   }
   
   public String getFocus(){
      return attachment.get();
   }

   @Override
   public void setFocus(String process) {
      attachment.set(process);
   }
   
   public boolean isFocused(ProcessEvent event) {
      String process = event.getProcess();
      String focus = attachment.get();
      
      if(focus != null) {
         return process.equals(focus);
      }
      return false;
   }
   
   public void clearFocus() {
      attachment.set(null);
   }

}
