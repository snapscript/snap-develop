package org.snapscript.develop.http.project;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root
public class ProjectDisplay {
   
   @Path("font")
   @Text
   private String font;

   @Path("font")
   @Attribute
   private int size;
   
   public ProjectDisplay(){
      this(null, 0);
   }
   
   public ProjectDisplay(String font, int size) {
      this.font = font;
      this.size = size;
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
