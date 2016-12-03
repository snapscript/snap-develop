package org.snapscript.agent.event;

public class ExecuteData {

   private final String process;
   private final String resource;
   private final String project;
   
   public ExecuteData(String process, String project, String resource) {
      this.project = project;
      this.resource = resource;
      this.process = process;
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
}
