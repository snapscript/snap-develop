package org.snapscript.agent.event;

public class RegisterEvent implements ProcessEvent {

   private String process;
   private String system;
   
   public RegisterEvent(String process, String system) {
      this.process = process;
      this.system = system;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   public String getSystem() {
      return system;
   }
}
