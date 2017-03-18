

package org.snapscript.develop.command;

public class FolderCollapseCommand implements Command {

   private String project;
   private String folder;
   
   public FolderCollapseCommand() {
      super();
   }
   
   public FolderCollapseCommand(String project, String folder) {
      this.project = project;
      this.folder = folder;
   }

   public String getProject() {
      return project;
   }

   public void setProject(String project) {
      this.project = project;
   }

   public String getFolder() {
      return folder;
   }

   public void setFolder(String folder) {
      this.folder = folder;
   }
}