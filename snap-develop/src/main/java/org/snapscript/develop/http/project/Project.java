package org.snapscript.develop.http.project;

import java.io.File;

import org.snapscript.develop.Workspace;

public class Project {
   
   private final ProjectFileSystem fileSystem;
   private final Workspace workspace;
   private final String projectName;
   private final String projectDirectory;

   public Project(Workspace workspace, String projectDirectory, String projectName) {
      this.fileSystem = new ProjectFileSystem(this);
      this.projectDirectory = projectDirectory;
      this.projectName = projectName;
      this.workspace = workspace;
   }

   public ProjectFileSystem getFileSystem() {
      return fileSystem;
   }

   public File getSourcePath() {
      try {
         return workspace.create(projectName);
      } catch (Exception e) {
         throw new IllegalStateException("Could not get source path for '" + projectName + "'");
      }
   }

   public File getProjectPath() {
      try {
         return workspace.create(projectName);
      } catch (Exception e) {
         throw new IllegalStateException("Could not get project path for '" + projectName + "'");
      }
   }
   
   public String getProjectDirectory() {
      return projectDirectory;
   }

   public String getProjectName() {
      return projectName;
   }
}
