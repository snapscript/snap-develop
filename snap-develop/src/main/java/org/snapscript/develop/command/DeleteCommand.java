
package org.snapscript.develop.command;

public class DeleteCommand implements Command {

   private String project;
   private String resource;
   
   public DeleteCommand() {
      super();
   }
   
   public DeleteCommand(String project, String resource) {
      this.resource = resource;
      this.project = project;
   }

   public String getProject() {
      return project;
   }

   public void setProject(String project) {
      this.project = project;
   }

   public String getResource() {
      return resource;
   }

   public void setResource(String resource) {
      this.resource = resource;
   }
}
