package org.snapscript.agent.event;

import java.util.Set;

public class BrowseEvent implements ProcessEvent {

   private Set<String> expand;
   private String process;
   private String thread;
   
   public BrowseEvent(String process, String thread, Set<String> expand) {
      this.process = process;
      this.thread = thread;
      this.expand = expand;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   public Set<String> getExpand() {
      return expand;
   }
   
   public String getThread() {
      return thread;
   }

}
