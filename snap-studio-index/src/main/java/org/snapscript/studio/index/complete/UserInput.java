package org.snapscript.studio.index.complete;

public class UserInput {
   
   private final String expression;
   private final String source;
   
   public UserInput(String source, String expression) {
      this.source = source;
      this.expression = expression;
   }
   
   public String getSource() {
      return source;
   }
   
   public String getExpression(){
      return expression;
   }
   
}