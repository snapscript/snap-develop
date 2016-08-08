package org.snapscript.agent.event;

public class PongEvent implements ProcessEvent {

   private String process;
   private String resource;
   private String system;
   private boolean running;
   
   public PongEvent(String process, String system) {
      this(process, system, null, false);
   }
   
   public PongEvent(String process, String system, String resource, boolean running) {
      this.resource = resource;
      this.process = process;
      this.running = running;
      this.system = system;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   public String getSystem() {
      return system;
   }
   
   public String getResource() {
      return resource;
   }
   
   public boolean isRunning() {
      return running;
   }
}
