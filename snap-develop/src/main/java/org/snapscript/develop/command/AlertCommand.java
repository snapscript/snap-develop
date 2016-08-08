package org.snapscript.develop.command;

public class AlertCommand implements Command {

   private String resource;
   private String message;
   
   public AlertCommand() {
      super();
   }
   
   public AlertCommand(String resource, String message) {
      this.resource = resource;
      this.message = message;
   }

   public String getResource() {
      return resource;
   }

   public void setResource(String resource) {
      this.resource = resource;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }
}
