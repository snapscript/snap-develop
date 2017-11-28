package org.snapscript.studio.index.complete;

public class EditContextExtractor {
   
   public static EditContext extractContext(CompletionRequest request) {
      String source = request.getSource();
      String completion = request.getComplete();
      String lines[] = source.split("\\r?\\n");
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
         String finished = SourceCodeFormatter.createSource(lines, line);
         char last = completion.charAt(length -1);
         
         if(Character.isWhitespace(last)) { // did user input end in a space?
            return new EditContext(finished, result + " ");
         }
         return new EditContext(finished, result);
      }
      return new EditContext(source, "");
   }


}
