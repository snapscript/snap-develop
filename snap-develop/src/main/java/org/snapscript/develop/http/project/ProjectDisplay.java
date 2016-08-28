package org.snapscript.develop.http.project;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root
public class ProjectDisplay {
   
   @Element
   private String theme;
   
   @Path("font")
   @Text
   private String font;

   @Path("font")
   @Attribute
   private int size;
   
   public ProjectDisplay(){
      this(null, null, 0);
   }
   
   public ProjectDisplay(String theme, String font, int size) {
      this.theme = theme;
      this.font = font;
      this.size = size;
   }
   
   public String getTheme() {
      return theme;
   }
   
   public void setTheme(String theme) {
      this.theme = theme;
   }

   public String getFont() {
      return font;
   }

   public void setFont(String font) {
      this.font = font;
   }

   public int getSize() {
      return size;
   }

   public void setSize(int size) {
      this.size = size;
   } 
}
