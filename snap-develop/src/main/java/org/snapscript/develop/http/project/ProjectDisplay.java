package org.snapscript.develop.http.project;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

@Root
public class ProjectDisplay {
   
   @Element(name="theme-name", required=false)
   private String themeName;
   
   @Element(name="console-capacity", required=false)
   private int consoleCapacity;
   
   @Path("font")
   @Element(name="font-family")
   private String fontName;

   @Path("font")
   @Element(name="font-size")
   private int fontSize;
   
   public ProjectDisplay(){
      this(null, null, 0, 50000);
   }
   
   public ProjectDisplay(String themeName, String fontName, int fontSize, int consoleCapacity) {
      this.consoleCapacity = consoleCapacity;
      this.themeName = themeName;
      this.fontName = fontName;
      this.fontSize = fontSize;
   }

   public String getThemeName() {
      return themeName;
   }

   public void setThemeName(String themeName) {
      this.themeName = themeName;
   }

   public int getConsoleCapacity() {
      return consoleCapacity;
   }

   public void setConsoleCapacity(int consoleCapacity) {
      this.consoleCapacity = consoleCapacity;
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
}
