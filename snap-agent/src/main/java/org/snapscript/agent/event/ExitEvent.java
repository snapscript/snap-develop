package org.snapscript.agent.event;

public class ExitEvent implements ProcessEvent {

   private String process;
   private long duration;
   
   public ExitEvent() {
      super();
   }
   
   public ExitEvent(String process, long duration) {
      this.duration = duration;
      this.process = process;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   public long getDuration() { // execute time
      return duration;
   }
   
   public void setDuration(long duration){
      this.duration = duration;
   }
}
