
package org.snapscript.develop.command;

public class ExitCommand implements Command {
   
   private String process;

   public ExitCommand() {
      super();
   }
   
   public ExitCommand(String process) {
      this.process = process;
   }

   public String getProcess() {
      return process;
   }

   public void setProcess(String process) {
      this.process = process;
   }  
}
