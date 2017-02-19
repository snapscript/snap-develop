/*
 * ProjectDisplay.java December 2016
 *
 * Copyright (C) 2016, Niall Gallagher <niallg@users.sf.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

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
