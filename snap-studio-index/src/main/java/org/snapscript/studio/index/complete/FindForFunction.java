package org.snapscript.studio.index.complete;

import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;

import org.snapscript.studio.index.IndexDatabase;
import org.snapscript.studio.index.IndexNode;

public class FindForFunction implements CompletionFinder {

   private static final Pattern PATTERN = Pattern.compile("\\s*([a-zA-Z0-9_]+)\\.([a-zA-Z0-9_]+)\\.$");
   
   @Override
   public UserText parseExpression(String expression) {
      return null;
   }

   @Override
   public Set<IndexNode> findMatches(IndexDatabase database, IndexNode node, UserText text) {
      return Collections.emptySet();
   }
}
