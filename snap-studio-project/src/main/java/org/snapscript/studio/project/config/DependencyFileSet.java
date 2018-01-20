package org.snapscript.studio.project.config;

import java.io.File;
import java.util.List;

public class DependencyFileSet {

   private final List<File> files;
   private final String message;
   private final String dependency;

   public DependencyFileSet(List<File> files, String dependency) {
      this(files, dependency, null);
   }
   
   public DependencyFileSet(List<File> files, String dependency, String message) {
      this.files = files;
      this.message = message;
      this.dependency = dependency;
   }
   
   public List<File> getFiles() {
      return files;
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
