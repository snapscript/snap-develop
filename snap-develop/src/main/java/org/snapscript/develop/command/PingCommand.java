package org.snapscript.develop.command;

public class PingCommand implements Command {
   
   private String project;

   public PingCommand() {
      super();
   }
   
   public PingCommand(String project) {
      this.project = project;
   }

   public String getProject() {
      return project;
   }

   public void setProject(String project) {
      this.project = project;
   }
}
