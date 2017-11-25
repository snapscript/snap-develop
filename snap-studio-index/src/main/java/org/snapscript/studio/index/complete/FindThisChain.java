package org.snapscript.studio.index.complete;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.snapscript.studio.index.IndexDatabase;
import org.snapscript.studio.index.IndexNode;

public class FindThisChain implements CompletionFinder {

   private static final Pattern PATTERN = Pattern.compile("\\s*this\\.([a-zA-Z0-9_]+)\\.([a-zA-Z0-9_]*)$");
   
   private final FindForVariable variable;
   
   public FindThisChain() {
      this.variable = new FindForVariable();
   }
   
   @Override
   public UserText parseExpression(String expression) {
      Matcher matcher = PATTERN.matcher(expression);
      
      if(matcher.matches()) {
         String name = matcher.group(1);
         String unfinished = matcher.group(2);
         return new UserText(name, unfinished);
      }
      return null;
   }

   @Override
   public Set<IndexNode> findMatches(IndexDatabase database, IndexNode node, UserText text) throws Exception {
      IndexNode typeNode = FindThis.findThisNode(node);
      
      if(typeNode != null) {
         Map<String, IndexNode> expandedScope = database.getMemberNodes(typeNode);
         String handle = text.getHandle();
         String unfinished = text.getUnfinished();
         IndexNode handleNode = expandedScope.get(handle);
         IndexNode constraintNode = FindForVariable.findConstraint(database, handleNode);
         
         return variable.findForHandle(database, constraintNode, unfinished);
      }
      return Collections.emptySet();
   }
}