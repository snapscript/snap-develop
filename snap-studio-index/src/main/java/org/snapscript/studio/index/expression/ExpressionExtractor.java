package org.snapscript.studio.index.expression;

public class ExpressionExtractor {
   
   public Expression extract(String expression) {
      return new ExpressionParser(expression);
   }
}
