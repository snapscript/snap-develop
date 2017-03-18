

package org.snapscript.develop.find;

public class MatchEvaluator {
   
   public static final String FOREGROUND_COLOR = "#ffffff";
   public static final String BACKGROUND_COLOR = "#6495ed"; 
   
   private final StringBuilder builder;
   private final String background;
   private final String foreground;
   private final String expression;
   private final String token;
   private final boolean bold;
   
   public MatchEvaluator(String expression) {
      this(expression, BACKGROUND_COLOR, FOREGROUND_COLOR, true);
   }
   
   public MatchEvaluator(String expression, String background, String foreground, boolean bold) {
      this.builder = new StringBuilder();
      this.token = expression.toLowerCase();
      this.background = background;
      this.foreground = foreground;
      this.expression = expression;
      this.bold = bold;
   }

   public String match(String line) {
      String source = line.toLowerCase();
      int index = source.indexOf(token);
      
      if(index >= 0) {
         int length = expression.length();
         int start = 0;
         
         while(index >= 0) {
            String begin = line.substring(start, index);
            String text = line.substring(index, index + length);
            
            builder.append(escape(begin));
            builder.append("<span style='background-color: ");
            builder.append(background);
            builder.append("; color: ");
            builder.append(foreground);
            builder.append("; font-weight: ");
            builder.append(bold ? "bold" : "normal");
            builder.append(";'>");
            builder.append(escape(text));
            builder.append("</span>");
            start = index + length;
            index = source.indexOf(token, start);
         }
         int last = line.length();
         String remainder = line.substring(start, last);
         builder.append(escape(remainder));
         String result = builder.toString();
         builder.setLength(0);
         return result;
      }
      return null;
   }
   
   private String escape(String token) {
      return token
            .replace("<", "&lt;")
            .replace(">", "&gt;");
   }
}

