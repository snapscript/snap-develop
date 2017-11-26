package org.snapscript.studio.index.complete;

import java.util.Set;

import org.snapscript.studio.index.IndexDatabase;
import org.snapscript.studio.index.IndexNode;
import org.snapscript.studio.index.expression.ExpressionFinder;

public class FindForExpression implements CompletionFinder {

   @Override
   public UserExpression parseExpression(String expression) {
      if(expression.contains(".")) {
         String text = expression.trim();
         return new UserExpression(text, null);
      }
      return null;
   }

   @Override
   public Set<IndexNode> findMatches(IndexDatabase database, IndexNode node, UserExpression text) throws Exception {
      ExpressionFinder finder = new ExpressionFinder(database);
      String expression = text.getHandle();
      return finder.find(node, expression);
   }

}
