package org.snapscript.agent.event;

public class EvaluateEvent implements ProcessEvent {

   private final String expression;
   private final String process;
   private final String thread;
   
   private EvaluateEvent(Builder builder) {
      this.expression = builder.expression;
      this.process = builder.process;
      this.thread = builder.thread;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   public String getExpression() {
      return expression;
   }
   
   public String getThread() {
      return thread;
   }

   public static class Builder {
      
      private String expression;
      private String process;
      private String thread;
      
      public Builder(String process) {
         this.process = process;
      }

      public Builder withThread(String thread) {
         this.thread = thread;
         return this;
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