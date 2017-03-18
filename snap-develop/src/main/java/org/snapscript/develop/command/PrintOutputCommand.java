
package org.snapscript.develop.command;

public class PrintOutputCommand implements Command {

   private String process;
   private String text;
   
   public PrintOutputCommand() {
      super();
   }
   
   public PrintOutputCommand(String process, String text) {
      this.process = process;
      this.text = text;
   }
   
   public String getText() {
      return text;
   }
   
   public void setText(String text) {
      this.text = text;
   }

   public String getProcess() {
      return process;
   }

   public void setProcess(String process) {
      this.process = process;
   } 
}
