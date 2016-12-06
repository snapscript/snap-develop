package org.snapscript.agent.event;

public class ExitEvent implements ProcessEvent {

   private final String process;
   private final long duration;

   private ExitEvent(Builder builder) {
      this.duration = builder.duration;
      this.process = builder.process;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   public long getDuration() { // execute time
      return duration;
   }

   public static class Builder {
      
      private String process;
      private long duration;
      
      public Builder(String process) {
         this.process = process;
      }

      public Builder withProcess(String process) {
         this.process = process;
         return this;
      }

      public Builder withDuration(long duration) {
         this.duration = duration;
         return this;
      }
      
      public ExitEvent build(){
         return new ExitEvent(this);
      }
      
   }
}
