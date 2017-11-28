package org.snapscript.studio.index.complete;

public class SourceCodeFormatter {

   /*
    * 1) Remove the comments without changing lines
    * 2) Replace empty compound statements with a no operation
    * 3) If the source is empty create an empty file
    */
   public static String createSource(String[] lines, int index) { // this method is really crap
      StringBuilder builder = new StringBuilder();
      boolean previousEndsWithBrace = false;
      
      lines[index -1] = "";
      
      for(String entry : lines) {
         String compact = entry.replaceAll("\\s+", ""); 
         
         if(entry.contains("//")) {
            if(compact.contains("{//") || compact.contains(")//")) {
               entry = entry.replaceAll("\\s*\\/\\/.*$", "");
            }
            compact = compact.replaceAll("\\s*\\/\\/.*$", "");
         }
         if(compact.endsWith("){")) { // if there is an open compound put in a no-op
            entry += ";";
         } 
         if(compact.startsWith("{") && previousEndsWithBrace) { // is this a new compound after a brace e.g if(x) {
            entry = entry.replaceFirst("\\{", "\\{;"); // inject a no-op
         }
         if(!compact.isEmpty()) { // ignore blank lines
            previousEndsWithBrace = compact.endsWith(")");
         }
         builder.append(entry);
         builder.append("\n");
      }
      String source = builder.toString();
      int length = source.trim().length();
      
      if(length == 0) {
         return "println();"; // provide some empty source  
      }
      return source;
   }
}
