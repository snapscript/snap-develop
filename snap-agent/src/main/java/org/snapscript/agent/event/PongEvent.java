package org.snapscript.agent.event;

public class PongEvent implements ProcessEvent {

   private String project;
   private String process;
   private String resource;
   private String system;
   private boolean running;
   
   public PongEvent(String process, String system) {
      this(process, system, null, null, false);
   }
   
   public PongEvent(String process, String system, String project, String resource, boolean running) {
      this.resource = resource;
      this.process = process;
      this.running = running;
      this.project = project;
      this.system = system;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   public String getProject() {
      return project;
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
