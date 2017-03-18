

package org.snapscript.develop.command;

public class DisplayUpdateCommand implements Command {

   private String project;
   private String themeName;
   private String fontName;
   private int fontSize;
   
   public DisplayUpdateCommand() {
      super();
   }
   
   public DisplayUpdateCommand(String project, String themeName, String fontName, int fontSize) {
      this.themeName = themeName;
      this.fontName = fontName;
      this.fontSize = fontSize;
      this.project = project;
   }

   public String getProject() {
      return project;
   }

   public void setProject(String project) {
      this.project = project;
   }

   public String getFontName() {
      return fontName;
   }

   public void setFontName(String fontName) {
      this.fontName = fontName;
   }

   public int getFontSize() {
      return fontSize;
   }

   public void setFontSize(int fontSize) {
      this.fontSize = fontSize;
   }

   public String getThemeName() {
      return themeName;
   }

   public void setThemeName(String themeName) {
      this.themeName = themeName;
   }
}
