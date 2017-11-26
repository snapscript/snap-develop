package org.snapscript.studio.index.complete;

public class UserExpression {

   private final String handle;
   private final String unfinished;
   
   public UserExpression(String handle, String unfinished) {
      this.handle = handle;
      this.unfinished = unfinished;
   }

   public String getHandle() {
      return handle;
   }

   public String getUnfinished() {
      return unfinished;
   }
   
   
}
