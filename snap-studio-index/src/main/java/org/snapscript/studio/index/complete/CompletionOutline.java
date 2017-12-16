package org.snapscript.studio.index.complete;

import org.snapscript.studio.index.IndexType;

public class CompletionOutline {

   private String resource;
   private String constraint;
   private IndexType type;
   private int line;
   
   public CompletionOutline(){
      super();
   }
   
   public CompletionOutline(IndexType type, String constraint, String resource, int line) {
      this.type = type;
      this.constraint = constraint;
      this.resource = resource;
      this.line = line;
   }

   public String getResource() {
      return resource;
   }

   public void setResource(String resource) {
      this.resource = resource;
   }

   public String getConstraint() {
      return constraint;
   }

   public void setConstraint(String constraint) {
      this.constraint = constraint;
   }

   public IndexType getType() {
      return type;
   }

   public void setType(IndexType type) {
      this.type = type;
   }

   public int getLine() {
      return line;
   }

   public void setLine(int line) {
      this.line = line;
   }

}
