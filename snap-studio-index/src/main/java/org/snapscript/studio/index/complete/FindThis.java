package org.snapscript.studio.index.complete;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.snapscript.studio.index.IndexDatabase;
import org.snapscript.studio.index.IndexNode;
import org.snapscript.studio.index.IndexSearcher;
import org.snapscript.studio.index.IndexType;

public class FindThis implements CompletionFinder {

   private static final Pattern PATTERN = Pattern.compile("\\s*this\\.([a-zA-Z0-9_]*)$");
   
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
      IndexNode typeNode = findTypeNode(node);
      
      if(typeNode != null) {
         Map<String, IndexNode> expandedScope = IndexSearcher.getNodesInScope(typeNode);
         String unfinished = text.getUnfinished();
         
         if(!expandedScope.isEmpty()) {
            Set<IndexNode> matched = new HashSet<IndexNode>();
            Set<Entry<String, IndexNode>> entries = expandedScope.entrySet();
            
            for(Entry<String, IndexNode> entry : entries) {
               IndexNode childNode = entry.getValue();
               IndexNode parentNode = childNode.getParent();
               
               if(parentNode == typeNode) {
                  IndexType type = childNode.getType();
                  String childName = childNode.getName();
               
                  if(type.isMemberFunction() || type.isProperty()) {
                     if(childName.startsWith(unfinished)) {
                        matched.add(childNode);
                     }
                  }
               
               }
            }
            return matched;
         }
      }
      return Collections.emptySet();
   }
   
   private IndexNode findTypeNode(IndexNode node) throws Exception {
      while(node != null) {
         IndexType type = node.getType();
         
         if(type.isType() && !type.isImport()) {
            return node;
         }
         node = node.getParent();
      }
      return node;
      
   }
}