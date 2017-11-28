package org.snapscript.studio.index.complete;

public class EditContext {
   
   private final String expression;
   private final String source;
   
   public EditContext(String source, String expression) {
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