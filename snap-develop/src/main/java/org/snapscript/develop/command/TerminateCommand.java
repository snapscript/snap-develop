
package org.snapscript.develop.command;

public class TerminateCommand implements Command {

   private String process;

   public TerminateCommand() {
      super();
   }
   
   public TerminateCommand(String process) {
      this.process = process;
   }

   public String getProcess() {
      return process;
   }

   public void setProcess(String process) {
      this.process = process;
   }  
}