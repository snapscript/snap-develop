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
import org.snapscript.studio.index.IndexSearcher;
import org.snapscript.studio.index.IndexType;

public class FindForVariable implements CompletionFinder {

   private static final Pattern PATTERN = Pattern.compile("\\s*([a-zA-Z0-9_]+)\\.([a-zA-Z0-9_]*)$");
   
   @Override
   public UserText parseExpression(String expression) {
      Matcher matcher = PATTERN.matcher(expression);
      
      if(matcher.matches()) {
         String handle = matcher.group(1);
         String unfinished = matcher.group(2);
         
         return new UserText(handle, unfinished);
      }
      return null;
   }

   @Override
   public Set<IndexNode> findMatches(IndexDatabase database, IndexNode node, UserText text) throws Exception {
      Map<String, IndexNode> expandedScope = database.getNodesInScope(node);
      String handle = text.getHandle();
      String unfinished = text.getUnfinished();
      IndexNode handleNode = findHandle(database, expandedScope, handle);
      
      return findForHandle(database, handleNode, unfinished);
   }
   
   public Set<IndexNode> findForHandle(IndexDatabase database, IndexNode handleNode, String unfinished) throws Exception {
      if(handleNode != null) {
         return findForNode(database, handleNode, unfinished);
      }
      return Collections.emptySet();
   }
   
   private Set<IndexNode> findForNode(IndexDatabase database, IndexNode handleNode, String unfinished) throws Exception {
      Set<IndexNode> matched = new HashSet<IndexNode>();
      Map<String, IndexNode> handleNodeScope = database.getMemberNodes(handleNode);
      Set<Entry<String, IndexNode>> entries = handleNodeScope.entrySet();
      
      for(Entry<String, IndexNode> entry : entries) {
         String name = entry.getKey();
         IndexNode childNode = entry.getValue();
         IndexType type = childNode.getType();
         
         if(name.startsWith(unfinished)) {
            if(type.isProperty() || type.isMemberFunction()) {
               matched.add(childNode);
            }
         }
      }
      return matched;
   }

   public static IndexNode findHandle(IndexDatabase database, Map<String, IndexNode> expandedScope, String handle) throws Exception {
      IndexNode handleNode = expandedScope.get(handle);
      
      if(handleNode != null) {
         handleNode = findConstraint(database, handleNode);
      }
      if(handleNode == null) {
         handleNode = database.getDefaultImport(null, handle);
      }
      return handleNode;
   }
   
   public static IndexNode findConstraint(IndexDatabase database, IndexNode handleNode) throws Exception {
      if(handleNode != null) {
         IndexNode constraintNode = handleNode.getConstraint();
         
         if(constraintNode != null) {
            handleNode = constraintNode;
         } else {
            IndexType type = handleNode.getType();
            
            if(type.isImport()) {
               String fullName = handleNode.getFullName();
               handleNode = database.getTypeNode(fullName);
               
               if(handleNode == null) {
                  handleNode = database.getDefaultImport(null, fullName);
               }
            }
         }
      }
      return handleNode;
   }
}
