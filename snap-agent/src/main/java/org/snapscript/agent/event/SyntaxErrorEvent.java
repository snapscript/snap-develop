package org.snapscript.agent.event;

public class SyntaxErrorEvent implements ProcessEvent {

   private String description;
   private String resource;
   private String process;
   private int line;
   
   public SyntaxErrorEvent(String process, String resource, String description, int line) {
      this.process = process;
      this.resource = resource;
      this.line = line;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   public String getDescription(){
      return description;
   }
      
   public String getResource() {
      return resource;
   }

   public int getLine() {
      return line;
   }
}
