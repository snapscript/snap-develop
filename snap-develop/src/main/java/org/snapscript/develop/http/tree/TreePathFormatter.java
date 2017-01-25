package org.snapscript.develop.http.tree;

public class TreePathFormatter {

   public static String formatPath(String project, String expand) {
      String expandPath = null;
      
      if(expand != null) {
         expandPath = expand;

         if(expandPath.startsWith("/")) {
            expandPath = expandPath.substring(1); 
         } 
         if(expandPath.endsWith("/")) {
            int length = expandPath.length();
            expandPath = expand.substring(0, length - 1);
         }
         expandPath = TreeConstants.ROOT + project + "/" + expandPath;
      }
      return expandPath;
   }
}
