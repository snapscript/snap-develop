package org.snapscript.agent.event;

public class BeginEvent implements ProcessEvent {

   private String resource;
   private String process;
   private String project;
   private long duration;
   
   public BeginEvent(String process, String project, String resource, long duration) {
      this.process = process;
      this.project = project;
      this.resource = resource;
      this.duration = duration;
   }

   public String getResource() {
      return resource;
   }

   public void setResource(String resource) {
      this.resource = resource;
   }

   @Override
   public String getProcess() {
      return process;
   }

   public void setProcess(String process) {
      this.process = process;
   }

   public String getProject() {
      return project;
   }

   public void setProject(String project) {
      this.project = project;
   }

   public long getDuration() { // compile time
      return duration;
   }

   public void setDuration(long duration) {
      this.duration = duration;
   }
}
