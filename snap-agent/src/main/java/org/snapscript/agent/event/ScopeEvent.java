package org.snapscript.agent.event;

import org.snapscript.agent.debug.ScopeVariableTree;

public class ScopeEvent implements ProcessEvent {
   
   public static final String SUSPENDED = "SUSPENDED";
   public static final String RUNNING = "RUNNING";

   private final ScopeVariableTree variables;
   private final String instruction;
   private final String status;
   private final String process;
   private final String resource;
   private final String thread;
   private final String stack;
   private final int line;
   private final int depth;
   private final int key;
   
   private ScopeEvent(Builder builder) {
      this.variables = builder.variables;
      this.instruction = builder.instruction;
      this.resource = builder.resource;
      this.process = builder.process;
      this.thread = builder.thread;
      this.status = builder.status;
      this.depth = builder.depth;
      this.stack = builder.stack;
      this.line = builder.line;
      this.key = builder.key;
   }
   
   @Override
   public String getProcess() {
      return process;
   }

   public ScopeVariableTree getVariables() {
      return variables;
   }

   public String getInstruction() {
      return instruction;
   }
   
   public String getStack(){
      return stack;
   }
   
   public String getResource() {
      return resource;
   }

   public String getStatus() {
      return status;
   }

   public String getThread() {
      return thread;
   }
   
   public int getDepth() {
      return depth;
   }

   public int getLine() {
      return line;
   }
   
   public int getKey() {
      return key;
   }
   
   public static class Builder {
      
      private ScopeVariableTree variables;
      private String instruction;
      private String status;
      private String process;
      private String resource;
      private String thread;
      private String stack;
      private int line;
      private int depth;
      private int key;
      
      public Builder(String process) {
         this.process = process;
      }

      public Builder withVariables(ScopeVariableTree variables) {
         this.variables = variables;
         return this;
      }

      public Builder withInstruction(String instruction) {
         this.instruction = instruction;
         return this;
      }

      public Builder withStatus(String status) {
         this.status = status;
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

      public Builder withThread(String thread) {
         this.thread = thread;
         return this;
      }

      public Builder withStack(String stack) {
         this.stack = stack;
         return this;
      }

      public Builder withLine(int line) {
         this.line = line;
         return this;
      }

      public Builder withDepth(int depth) {
         this.depth = depth;
         return this;
      }

      public Builder withKey(int key) {
         this.key = key;
         return this;
      }
      
      public ScopeEvent build() {
         return new ScopeEvent(this);
      }
   }
}