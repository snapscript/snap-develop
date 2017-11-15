package org.snapscript.studio.index.complete;

import java.util.Set;

import org.snapscript.studio.index.IndexDatabase;
import org.snapscript.studio.index.IndexNode;

public interface CompletionFinder {
   UserText parseExpression(String expression);
   Set<IndexNode> findMatches(IndexDatabase database, IndexNode node, UserText text) throws Exception;
}
