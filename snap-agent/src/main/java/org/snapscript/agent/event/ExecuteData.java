package org.snapscript.agent.event;

public class ExecuteData {

   private final String process;
   private final String resource;
   private final String project;
   private final boolean debug;
   
   public ExecuteData(String process, String project, String resource, boolean debug) {
      this.project = project;
      this.resource = resource;
      this.process = process;
      this.debug = debug;
   }

   public String getProcess() {
      return process;
   }
   
   public String getResource() {
      return resource;
   }

   public String getProject() {
      return project;
   }
   
   public boolean isDebug(){
      return debug;
   }
}