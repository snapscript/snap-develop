package org.snapscript.agent.event;

public class EvaluateEvent implements ProcessEvent {

   private final String expression;
   private final String process;
   
   private EvaluateEvent(Builder builder) {
      this.expression = builder.expression;
      this.process = builder.process;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   public String getExpression() {
      return expression;
   }
   
   public static class Builder {
      
      private String expression;
      private String process;
      
      public Builder(String process) {
         this.process = process;
      }

      public Builder withExpression(String expression) {
         this.expression = expression;
         return this;
      }

      public Builder withProcess(String process) {
         this.process = process;
         return this;
      }
      
      public EvaluateEvent build(){
         return new EvaluateEvent(this);
      }
   }
}