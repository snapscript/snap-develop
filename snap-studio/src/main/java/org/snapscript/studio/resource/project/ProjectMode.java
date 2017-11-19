package org.snapscript.studio.resource.project;

public class ProjectMode {

   public static final String SINGLE_MODE = "debug";
   public static final String DEVELOP_MODE = "develop";
   
   private final String mode;
   
   public ProjectMode(String mode) {
      this.mode = mode;
   }
   
   public boolean isSingleMode() {
      return mode.equals(SINGLE_MODE);
   }
   
   public boolean isMultipleMode() {
      return mode.equals(DEVELOP_MODE);
   }
   
   public String getMode() {
      return mode;
   }
}