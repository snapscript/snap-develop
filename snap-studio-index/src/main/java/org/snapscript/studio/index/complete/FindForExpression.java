package org.snapscript.studio.index.complete;

import java.util.Set;

import org.snapscript.studio.index.IndexDatabase;
import org.snapscript.studio.index.IndexNode;
import org.snapscript.studio.index.expression.ExpressionFinder;

public class FindForExpression implements CompletionFinder {

   @Override
   public InputExpression parseExpression(EditContext context) {
      String expression = context.getExpression();
      
      if(expression.contains(".")) {
         String text = expression.trim();
         return new InputExpression(text, null);
      }
      return null;
   }

   @Override
   public Set<IndexNode> findMatches(IndexDatabase database, IndexNode node, InputExpression text) throws Exception {
      ExpressionFinder finder = new ExpressionFinder(database);
      String expression = text.getHandle();
      return finder.find(node, expression);
   }

}
