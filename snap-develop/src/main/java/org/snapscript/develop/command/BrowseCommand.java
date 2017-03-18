
package org.snapscript.develop.command;

import java.util.Set;

public class BrowseCommand implements Command {

   private Set<String> expand;
   private String thread;
   
   public BrowseCommand() {
      super();
   }
   
   public BrowseCommand(String thread, Set<String> expand) {
      this.thread = thread;
      this.expand = expand;
   }

   public Set<String> getExpand() {
      return expand;
   }

   public void setExpand(Set<String> expand) {
      this.expand = expand;
   }

   public String getThread() {
      return thread;
   }

   public void setThread(String thread) {
      this.thread = thread;
   }
}
