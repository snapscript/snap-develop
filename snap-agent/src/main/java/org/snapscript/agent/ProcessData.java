package org.snapscript.agent;

public class ProcessData {

   private final String resource;
   private final String project;
   
   public ProcessData(String project, String resource) {
      this.project = project;
      this.resource = resource;
   }

   public String getResource() {
      return resource;
   }

   public String getProject() {
      return project;
   }
}