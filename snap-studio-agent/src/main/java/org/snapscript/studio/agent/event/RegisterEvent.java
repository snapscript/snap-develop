package org.snapscript.studio.agent.event;

import static org.snapscript.studio.agent.core.ExecuteStatus.REGISTERING;

import org.snapscript.studio.agent.core.ExecuteStatus;

public class RegisterEvent implements ProcessEvent {

   private final String process;
   private final String system;
   
   private RegisterEvent(Builder builder) {
      this.process = builder.process;
      this.system = builder.system;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   public String getSystem() {
      return system;
   }
   
   public ExecuteStatus getStatus() {
      return REGISTERING;
   }
   
   public static class Builder {
      
      private String process;
      private String system;
      
      public Builder(String process) {
         this.process = process;
      }

      public Builder withProcess(String process) {
         this.process = process;
         return this;
      }

      public Builder withSystem(String system) {
         this.system = system;
         return this;
      }
      
      public RegisterEvent build(){
         return new RegisterEvent(this);
      }
   }
}