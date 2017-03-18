
package org.snapscript.develop.command;

public class ExploreCommand implements Command {

   private String resource;
   private String project;
   
   public ExploreCommand() {
      super();
   }
   
   public ExploreCommand(String project, String resource, String source) {
      this.project = project;
      this.resource = resource;
   }

   public String getResource() {
      return resource;
   }

   public void setResource(String resource) {
      this.resource = resource;
   }

   public String getProject() {
      return project;
   }

   public void setProject(String project) {
      this.project = project;
   }
}
