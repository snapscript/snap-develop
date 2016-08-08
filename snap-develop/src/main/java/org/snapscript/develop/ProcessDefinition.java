package org.snapscript.develop;

public class ProcessDefinition {

   private final Process process;
   private final String name;
   
   public ProcessDefinition(Process process, String name) {
      this.process = process;
      this.name = name;
   }
   
   public Process getProcess() {
      return process;
   }
   
   public String getName() {
      return name;
   }
}
