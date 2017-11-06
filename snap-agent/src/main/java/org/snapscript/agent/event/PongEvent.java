package org.snapscript.agent.event;

public class PongEvent implements ProcessEvent {

   private final String project;
   private final String process;
   private final String resource;
   private final String system;
   private final boolean running;
   private final boolean debug;
   private final long totalMemory;
   private final long usedMemory;
   private final int threads;

   public PongEvent(Builder builder) {
      this.totalMemory = builder.totalMemory;
      this.usedMemory = builder.usedMemory;
      this.threads = builder.threads;
      this.resource = builder.resource;
      this.process = builder.process;
      this.running = builder.running;
      this.project = builder.project;
      this.system = builder.system;
      this.debug = builder.debug;
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
   
   public long getUsedMemory() {
      return usedMemory;
   }

   public long getTotalMemory() {
      return totalMemory;
   }

   public int getThreads() {
      return threads;
   }
   
   public boolean isRunning() {
      return running;
   }
   
   public boolean isDebug() {
      return debug;
   }
   
   public static class Builder {
      
      private String project;
      private String process;
      private String resource;
      private String system;
      private boolean running;
      private boolean debug;
      private long totalMemory;
      private long usedMemory;
      private int threads;
   
      public Builder(String process) {
         this.process = process;
      }

      public Builder withProject(String project) {
         this.project = project;
         return this;
      }

      public Builder withProcess(String process) {
         this.process = process;
         return this;
      }

      public Builder withResource(String resource) {
         this.resource = resource;
         return this;
      }

      public Builder withSystem(String system) {
         this.system = system;
         return this;
      }

      public Builder withRunning(boolean running) {
         this.running = running;
         return this;
      }
      
      public Builder withThreads(int threads){
         this.threads = threads;
         return this;
      }
      
      public Builder withTotalMemory(long totalMemory){
         this.totalMemory = totalMemory;
         return this;
      }
      
      public Builder withUsedMemory(long usedMemory){
         this.usedMemory = usedMemory;
         return this;
      }
      
      public Builder withDebug(boolean debug) {
         this.debug = debug;
         return this;
      }
      
      public PongEvent build() {
         return new PongEvent(this);
      }
   }
}