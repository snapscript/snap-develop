
package org.snapscript.develop.command;

public class BeginCommand implements Command {

   private String resource;
   private String process;
   private long duration;
   
   public BeginCommand() {
      super();
   }
   
   public BeginCommand(String process, String resource, long duration) {
      this.process = process;
      this.resource = resource;
      this.duration = duration;
   }

   public String getResource() {
      return resource;
   }

   public void setResource(String resource) {
      this.resource = resource;
   }

   public String getProcess() {
      return process;
   }

   public void setProcess(String process) {
      this.process = process;
   }

   public long getDuration() { // compile time
      return duration;
   }

   public void setDuration(long duration) {
      this.duration = duration;
   }
}
