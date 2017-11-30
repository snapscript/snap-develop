package org.snapscript.studio.index.complete;

public class EditContextExtractor {
   
   private static final char[] TOKEN = "){}".toCharArray();
   
   public static EditContext extractContext(CompletionRequest request) {
      String source = request.getSource();
      String completion = request.getComplete();
      String lines[] = cleanSource(source);
      int length = completion.length();
      int line = request.getLine();

      if(length > 0) {
         if(lines.length < line) {
            String[] copy = new String[line];

            for(int i = 0; i < lines.length; i++) {
               copy[i] = lines[i];
            }
            for(int i = lines.length; i < line; i++) {
               copy[i] = "";
            }
            lines = copy;
         }
         lines[line - 1] = completion; // insert expression at line
         String result = InputExpressionParser.parseLine(lines, line);
         String finished = generateSource(lines, line);
         char last = completion.charAt(length -1);
         
         if(Character.isWhitespace(last)) { // did user input end in a space?
            return new EditContext(finished, completion, result + " ");
         }
         return new EditContext(finished, completion, result);
      }
      return new EditContext(source, completion, "");
   }
   

   private static String[] cleanSource(String source) {
      char[] array = source.toCharArray();
      CommentStripper cleaner = new CommentStripper(array);
      String clean = cleaner.clean();
      
      return clean.split("\\r?\\n");
   }
   
   private static String generateSource(String[] lines, int index) { 
      StringBuilder builder = new StringBuilder();
      
      lines[index -1] = "";
      
      for(String entry : lines) {
         builder.append(entry);
         builder.append("\n");
      }
      String source = builder.toString();
      String trim = source.trim();
      
      if(!trim.isEmpty()) {
         char[] array = source.toCharArray();
         int seek = 0;
         
         builder.setLength(0);
         
         for(int i = 0; i < array.length; i++) {
            char next = array[i];
            
            if(!Character.isWhitespace(next)) {
               if(next != TOKEN[seek++]) {
                  seek = 0;
               }
               if(seek >= TOKEN.length) {
                  char prev = array[i-1];
                  
                  if(Character.isWhitespace(prev)) {
                     builder.append(";"); // add a no-op statement
                  }
                  seek = 0;
               }
            }
            builder.append(next);
         }
         return builder.toString();
      }
      return "println();";
   }
}
