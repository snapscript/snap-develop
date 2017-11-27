package org.snapscript.studio.index.complete;

import static org.snapscript.studio.index.expression.ExpressionBraceType.isCloseBrace;
import static org.snapscript.studio.index.expression.ExpressionBraceType.isOpenBrace;
import static org.snapscript.studio.index.expression.ExpressionBraceType.resolveBraceType;

import org.snapscript.common.ArrayStack;
import org.snapscript.common.Stack;
import org.snapscript.studio.index.expression.ExpressionBraceType;

public class UserInputExtractor {
   
   public static UserInput extractInput(CompletionRequest request) {
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
         String result = parseLine(lines, line);
         String finished = createSource(lines, line);
         char last = completion.charAt(length -1);
         
         if(isSpace(last)) {
            return new UserInput(finished, result + " ");
         }
         return new UserInput(finished, result);
      }
      return new UserInput(source, "");
   }
   
   private static String createSource(String[] lines, int index) {
      StringBuilder builder = new StringBuilder();
      
      lines[index -1] = "";
      
      for(String entry : lines) {
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

   public static String parseLine(String source, int index) { // for testing
      String lines[] = source.split("\\r?\\n");
      String expression = parseLine(lines, index);
      
      return expression.trim();
   }
   
   private static String parseLine(String[] lines, int index) {
      Stack<ExpressionBraceType> braces = new ArrayStack<ExpressionBraceType>();
      Stack<Character> quotes = new ArrayStack<Character>();
      StringBuilder builder = new StringBuilder();
      
      if(lines.length >= index) {
         for(int i = index; i > 0; i--) {
            String trimmed = lines[i - 1].trim(); // lines start at 1
            int length = trimmed.length();
            int begin = length -1;
            
            while(begin >= 0) {
               char next = trimmed.charAt(begin);
               
               if(!quotes.isEmpty()) {
                  if(isQuote(next)) {
                     char top = quotes.peek();
                     
                     if(top == next) { // have we closed the quotes
                        int seek = begin -1;
                        int escape = 0;
                        
                        while(seek >= 0) {
                           char previous = trimmed.charAt(seek);
                           
                           if(previous != '\'') {
                              break;
                           }
                           seek--;
                        }
                        if(escape % 2 == 0) { // there was even or no braces
                           quotes.pop();
                        }
                     } 
                  }
               } else {
                  if(isQuote(next)) {
                     quotes.push(next);
                  } else if(isCloseBrace(next)) {
                     ExpressionBraceType type = resolveBraceType(next);

                     if(isTerminal(next)) { 
                        int size = braces.size();
                        
                        if(size == 0) {
                           return builder.toString();
                        }
                     }
                     braces.push(type);
                  } else if(isOpenBrace(next)) {
                     int current = builder.length();
                     int size = braces.size();
                     
                     if(size == 0) { // no braces in stack
                        if(current > 0 || i != index) { // we have something or new lines
                           return builder.toString();
                        }
                     } else {
                        ExpressionBraceType top = braces.peek();
                        
                        if(top.open != next) {
                           return builder.toString();
                        }
                        braces.pop(); // remove brace
                     }
                  } else if(isTerminal(next)) {
                     int size = braces.size();
                     
                     if(size == 0) {
                        return builder.toString();
                     }
                  }
               }
               builder.insert(0, next);
               begin--;
            }
            lines[i - 1] = ""; // clear the expression
         }
      }
      return builder.toString();
   }
   
   private static boolean isSpace(char value) {
      switch(value){
      case ' ': case '\t':
         return true;
      }
      return false;
   }
   
   private static boolean isQuote(char value) {
      switch(value){
      case '"': case '\'':
      case '`':
         return true;
      }
      return false;
   }
   
   private static boolean isTerminal(char value) {
      switch(value) {
      case ',': case '{':
      case '(': case '+':
      case '-': case '*':
      case '/': case '%':
      case '|': case '&':
      case '?': case ':':
      case '=': case '<':
      case '>': case ';':
      case '}':   
         return true;
      }
      return false;
   }
}
