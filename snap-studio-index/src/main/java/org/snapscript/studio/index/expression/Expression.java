package org.snapscript.studio.index.expression;

import java.util.List;

public interface Expression {
   String getExpression();
   List<ExpressionToken> getTokens();
}
