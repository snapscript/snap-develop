package org.snapscript.studio.project.config;

import java.io.File;

public class DependencyFile {

   private final File file;
   private final String message;
   private final String dependency;

   public DependencyFile(File file) {
      this(file, null);
   }
   
   public DependencyFile(File file, String dependency) {
      this(file, dependency, null);
   }
   
   public DependencyFile(File file, String dependency, String message) {
      this.file = file;
      this.message = message;
      this.dependency = dependency;
   }
   
   public File getFile() {
      return file;
   }
   
   public String getDependency(){ 
      return dependency;
   }
   
   public String getMessage() {
      return message;
   }
   
   @Override
   public String toString() {
      return String.format("%s: %s", dependency, message);
   }
}
