
package org.snapscript.develop.command;

public class PrintErrorCommand implements Command {

   private String process;
   private String text;
   
   public PrintErrorCommand() {
      super();
   }
   
   public PrintErrorCommand(String process, String text) {
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
