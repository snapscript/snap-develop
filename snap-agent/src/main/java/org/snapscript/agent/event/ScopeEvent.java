package org.snapscript.agent.event;

import java.util.Map;

import org.snapscript.agent.debug.ScopeVariableTree;

public class ScopeEvent implements ProcessEvent {
   
   public static final String SUSPENDED = "SUSPENDED";
   public static final String RUNNING = "RUNNING";

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
   
   public ScopeEvent(String process, ScopeVariableTree variables, String thread, String stack, String instruction, String status, String resource, int line, int depth, int key) {
      this.variables = variables;
      this.instruction = instruction;
      this.resource = resource;
      this.process = process;
      this.thread = thread;
      this.status = status;
      this.depth = depth;
      this.stack = stack;
      this.line = line;
      this.key = key;
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
}
