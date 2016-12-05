package org.snapscript.develop.http.project;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

@Root
public class ProjectDisplay {
   
   @Element(name="theme-name", required=false)
   private String themeName;
   
   @Element(name="logo-image", required=false)
   private String logoImage;
   
   @Element(name="console-capacity", required=false)
   private int consoleCapacity;
   
   @Path("font")
   @Element(name="font-family")
   private String fontName;

   @Path("font")
   @Element(name="font-size")
   private int fontSize;
   
   public ProjectDisplay(){
      this(null, null, null, 0, 50000);
   }
   
   public ProjectDisplay(String themeName, String logoImage, String fontName, int fontSize, int consoleCapacity) {
      this.consoleCapacity = consoleCapacity;
      this.logoImage = logoImage;
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

   public String getLogoImage() {
      return logoImage;
   }

   public void setLogoImage(String logoImage) {
      this.logoImage = logoImage;
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
