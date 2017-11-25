package org.snapscript.studio.index.complete;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.snapscript.studio.index.IndexDatabase;
import org.snapscript.studio.index.IndexNode;
import org.snapscript.studio.index.IndexType;

public class FindInScopeMatching implements CompletionFinder {

   private static final Pattern PATTERN = Pattern.compile("\\s*([a-zA-Z0-9_]*)$");
   
   @Override
   public UserText parseExpression(String expression) {
      Matcher matcher = PATTERN.matcher(expression);
      
      if(matcher.matches()) {
         String unfinished = matcher.group(1);
         return new UserText(null, unfinished);
      }
      return null;
   }

   @Override
   public Set<IndexNode> findMatches(IndexDatabase database, IndexNode node, UserText text) throws Exception {
      Map<String, IndexNode> expandedScope = database.getNodesInScope(node);
      Set<Entry<String, IndexNode>> entries = expandedScope.entrySet();
      String unfinished = text.getUnfinished();
      
      if(!entries.isEmpty()) {
         Set<IndexNode> matched = new HashSet<IndexNode>();
         
         for(Entry<String, IndexNode> entry : entries) {
            String name = entry.getKey();
            IndexNode value = entry.getValue();
            IndexType type = value.getType();
            
            if(!type.isImport()) {
               if(type.isType() || type.isConstrained()) {
                  if(name.startsWith(unfinished)) {
                     matched.add(value);
                  }
               }
            }
         }
         return matched;
      }
      return Collections.emptySet();
   }
}
