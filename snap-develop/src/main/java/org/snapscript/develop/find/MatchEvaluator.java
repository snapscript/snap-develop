

package org.snapscript.develop.find;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

public class MatchEvaluator {
   
   public static final String FOREGROUND_COLOR = "#ffffff";
   public static final String BACKGROUND_COLOR = "#6495ed"; 
   
   protected final StringBuilder builder;
   protected final String background;
   protected final String foreground;
   protected final String expression;
   protected final boolean bold;
   
   public MatchEvaluator(String expression) {
      this(expression, BACKGROUND_COLOR, FOREGROUND_COLOR, true);
   }
   
   public MatchEvaluator(String expression, String background, String foreground, boolean bold) {
      this.builder = new StringBuilder();
      this.background = background;
      this.foreground = foreground;
      this.expression = expression;
      this.bold = bold;
   }

   public String match(String line, boolean caseSensitive) {
      if(!caseSensitive) {
         String source = line.toLowerCase();
         String token = expression.toLowerCase();
         List<MatchPart> tokens = match(line, source, expression, token);
         
         if(tokens != null) {
            return highlightText(builder, tokens);
         }
         return null;
      }
      List<MatchPart> tokens = match(line, line, expression, expression);
      
      if(tokens != null) {
         return highlightText(builder, tokens);
      }
      return null;
   }
   
   public String replace(String line, String replace, boolean caseSensitive) {
      if(!caseSensitive) {
         String source = line.toLowerCase();
         String token = expression.toLowerCase();
         List<MatchPart> tokens = match(line, source, expression, token);
         
         if(tokens != null) {
            return replaceText(builder, replace, tokens);
         }
         return null;
      }
      List<MatchPart> tokens = match(line, line, expression, expression);
      
      if(tokens != null) {
         return replaceText(builder, replace, tokens);
      }
      return null;
   }
   
   private String replaceText(StringBuilder builder, String replace, List<MatchPart> list) {
      for(MatchPart part : list) {
         String begin = part.getBegin();
         String text = part.getMatch();
         
         builder.append(begin);
         
         if(text != null) {
            builder.append(replace);
         }
      }
      String text = builder.toString();
      builder.setLength(0);
      return text;
   }
   
   private String highlightText(StringBuilder builder, List<MatchPart> list) {
      for(MatchPart part : list) {
         String begin = part.getBegin();
         String text = part.getMatch();
         
         builder.append(escape(begin));
         
         if(text != null) {
            builder.append("<span style='background-color: ");
            builder.append(background);
            builder.append("; color: ");
            builder.append(foreground);
            builder.append("; font-weight: ");
            builder.append(bold ? "bold" : "normal");
            builder.append(";'>");
            builder.append(escape(text));
            builder.append("</span>");
         }
      }
      String text = builder.toString();
      builder.setLength(0);
      return text;
   }
   
   protected List<MatchPart> match(String line, String source, String expression, String token) {
      int index = source.indexOf(token);
      
      if(index >= 0) {
         List<MatchPart> tokens = new ArrayList<MatchPart>();
         int length = expression.length();
         int start = 0;
         
         while(index >= 0) {
            String begin = line.substring(start, index);
            String text = line.substring(index, index + length);
            
            tokens.add(new MatchPart(begin, text));
            start = index + length;
            index = source.indexOf(token, start);
         }
         int last = line.length();
         String remainder = line.substring(start, last);
         tokens.add(new MatchPart(remainder, null));
         return tokens;
      }
      return null;
   }
   
   private static String escape(String token) {
      return token
            .replace("<", "&lt;")
            .replace(">", "&gt;");
   }
   
   @Data
   @AllArgsConstructor
   public static class MatchPart {
      
      private final String begin;
      private final String match;
   }
}

