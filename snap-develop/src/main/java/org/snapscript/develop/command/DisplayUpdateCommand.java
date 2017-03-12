/*
 * DisplayUpdateCommand.java December 2016
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
