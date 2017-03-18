
package org.snapscript.develop.command;

public class RenameCommand implements Command {

   private String project;
   private String from;
   private String to;
   
   public RenameCommand() {
      super();
   }
   
   public RenameCommand(String project, String from, String to) {
      this.project = project;
      this.from = from;
      this.to = to;
   }

   public String getProject() {
      return project;
   }

   public void setProject(String project) {
      this.project = project;
   }

   public String getFrom() {
      return from;
   }

   public void setFrom(String from) {
      this.from = from;
   }

   public String getTo() {
      return to;
   }

   public void setTo(String to) {
      this.to = to;
   }
}
