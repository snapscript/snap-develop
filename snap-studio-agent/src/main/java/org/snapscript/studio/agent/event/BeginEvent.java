package org.snapscript.studio.agent.event;

import org.snapscript.studio.agent.ProcessMode;

public class BeginEvent implements ProcessEvent {

   private final ProcessMode mode;
   private final String resource;
   private final String process;
   private final String project;
   private final long duration;
   private final boolean debug;
   
   private BeginEvent(Builder builder) {
      this.process = builder.process;
      this.project = builder.project;
      this.resource = builder.resource;
      this.duration = builder.duration;
      this.mode = builder.mode;
      this.debug = builder.debug;
   }
   
   public ProcessMode getMode() {
      return mode;
   }

   public String getResource() {
      return resource;
   }

   public String getProcess() {
      return process;
   }

   public String getProject() {
      return project;
   }

   public long getDuration() {
      return duration;
   }
   
   public boolean isDebug(){
      return debug;
   }

   public static class Builder {
      
      private ProcessMode mode;
      private String resource;
      private String process;
      private String project;
      private long duration;
      private boolean debug;
      
      public Builder(String process){
         this.process = process;
      }
      
      public Builder withMode(ProcessMode mode) {
         this.mode = mode;
         return this;
      }

      public Builder withResource(String resource) {
         this.resource = resource;
         return this;
      }

      public Builder withProcess(String process) {
         this.process = process;
         return this;
      }

      public Builder withProject(String project) {
         this.project = project;
         return this;
      }

      public Builder withDuration(long duration) {
         this.duration = duration;
         return this;
      }
      
      public Builder withDebug(boolean debug) {
         this.debug = debug;
         return this;
      }
      
      public BeginEvent build(){
         return new BeginEvent(this);
      }
      
      
   }
}