package org.snapscript.studio.index.complete;

import java.util.Set;

import org.snapscript.studio.index.IndexDatabase;
import org.snapscript.studio.index.IndexNode;

public interface CompletionFinder {
   InputExpression parseExpression(EditContext context);
   Set<IndexNode> findMatches(IndexDatabase database, IndexNode node, InputExpression text) throws Exception;
}
