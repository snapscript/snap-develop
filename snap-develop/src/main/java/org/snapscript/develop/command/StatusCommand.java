package org.snapscript.develop.command;

public class StatusCommand implements Command {

   private String resource;
   private String process;
   private String system;
   private boolean focus;
   private boolean running;
   private long time;
   
   public StatusCommand() {
      super();
   }
   
   public StatusCommand(String process, String system, String resource, long time, boolean running, boolean focus) {
      this.running = running;
      this.process = process;
      this.resource = resource;
      this.system = system;
      this.focus = focus;
      this.time = time;
   }
   
   public long getTime() {
      return time;
   }
   
   public void setTime(long time) {
      this.time = time;
   }
   
   public String getSystem() {
      return system;
   }
   
   public void setSystem(String system) {
      this.system = system;
   }

   public String getProcess() {
      return process;
   }

   public void setProcess(String process) {
      this.process = process;
   }

   public String getResource() {
      return resource;
   }

   public void setResource(String resource) {
      this.resource = resource;
   }

   public boolean isRunning() {
      return running;
   }

   public void setRunning(boolean running) {
      this.running = running;
   }
   
   public boolean isFocus() {
      return focus;
   }

   public void setFocus(boolean focus) {
      this.focus = focus;
   }
}