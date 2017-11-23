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
      Map<String, IndexNode> expandedScope = IndexSearcher.getNodesInScope(node);
      String handle = text.getHandle();
      IndexNode handleNode = findHandle(database, expandedScope, handle);
      
      if(handleNode != null) {
         if(handleNode.isNative()) {
            return findForNativeNode(database, handleNode, text);
         }
         return findForNode(database, handleNode, text);
      }
      return Collections.emptySet();
   }
   
   private Set<IndexNode> findForNativeNode(IndexDatabase database, IndexNode handleNode, UserText text) throws Exception {
      Set<IndexNode> matched = new HashSet<IndexNode>();
      Map<String, IndexNode> handleNodeScope = IndexSearcher.getNodesInScope(handleNode);
      Set<Entry<String, IndexNode>> entries = handleNodeScope.entrySet();
      String unfinished = text.getUnfinished();
      
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
   
   private Set<IndexNode> findForNode(IndexDatabase database, IndexNode handleNode, UserText text) throws Exception {
      Set<IndexNode> matched = new HashSet<IndexNode>();
      Map<String, IndexNode> handleNodeScope = IndexSearcher.getNodesInScope(handleNode);
      Set<Entry<String, IndexNode>> entries = handleNodeScope.entrySet();
      String fullName = handleNode.getFullName();
      String unfinished = text.getUnfinished();
      
      for(Entry<String, IndexNode> entry : entries) {
         String name = entry.getKey();
         IndexNode childNode = entry.getValue();
         IndexType type = childNode.getType();
         
         if(name.startsWith(unfinished) && !type.isImport()) {
            if(type.isType()) {
               IndexNode parent = childNode.getParent();
               
               if(parent != null) {
                  String parentName = parent.getFullName();
                  
                  if(fullName.equals(parentName)) { // is the parent the same as the handleNode
                     matched.add(childNode);
                  }
               } 
            } else if(!type.isConstructor()) {
               matched.add(childNode);
            }
         }
      }
      return matched;
   }

   private IndexNode findHandle(IndexDatabase database, Map<String, IndexNode> expandedScope, String handle) throws Exception {
      IndexNode handleNode = expandedScope.get(handle);
      
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
      if(handleNode == null) {
         handleNode = database.getDefaultImport(null, handle);
      }
      return handleNode;
   }
}
