package org.snapscript.index;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.snapscript.parse.Token;

public class IndexSearcher {

   private final IndexNodeComparator comparator;
   private final IndexBraceCounter counter;
   private final IndexNode node;
   
   public IndexSearcher(IndexNode node, List<Token> tokens) {
      this.comparator = new IndexNodeComparator(true);
      this.counter = new IndexBraceCounter(tokens);
      this.node = node;
   }
   
   public IndexNode getNode(){
      return node;
   }
   
   public IndexNode getNode(int line) {
      int depth = counter.getDepth(line);
      
      if(depth > 0) {
         return getNode(node, line, depth);
      }
      return node;
   }
   
   public Map<String, IndexNode> getNodesInScope(int line) {
      IndexNode node = getNode(line);
      
      if(node != null) {
         Map<String, IndexNode> scope = new HashMap<String, IndexNode>();
         
         while(node != null) {
            Set<IndexNode> nodes = node.getNodes();
            
            for(IndexNode entry : nodes) {
               Index index = entry.getIndex();
               String name = index.getName();
               
               scope.put(name, entry);
            }
            node = node.getParent();
         }
         return scope;
      }
      return Collections.emptyMap();
   }
   
   public int getDepth(int line) {
      return counter.getDepth(line);
   }
   
   private IndexNode getNode(IndexNode node, int line, int depth) {
      Index index = node.getIndex();
      int start = index.getLine();
      
      if(depth == 0 && start <= line) {
         return getBestNode(node, line);
      } 
      if(depth > 0){
         Set<IndexNode> nodes = node.getNodes();
         
         if(!nodes.isEmpty()) {
            Set<IndexNode> results = new TreeSet<IndexNode>(comparator);
            
            for(IndexNode entry : nodes) {
               IndexType type = entry.getIndex().getType();
               
               if(!type.isLeaf()) {
                  IndexNode match = getNode(entry, line, depth -1);
                  
                  if(match != null) {
                     results.add(match);
                  }
               }
            }
            if(!results.isEmpty()) {
               return results.iterator().next();
            }
         }
      }
      return null;
   }
      
   public IndexNode getBestNode(IndexNode node, int line) {
      Set<IndexNode> nodes = node.getNodes();
      IndexNode best = node;
      
      for(IndexNode entry : nodes) {
         Index index = entry.getIndex();
         IndexType type = index.getType();
         int start = index.getLine();
         
         if(start <= line) {
            if(!type.isLeaf()) {
               int threshold = best.getIndex().getLine();
   
               if(start >= threshold) {
                  best = entry;
               }
            }
         }
      }
      return best;
   }
}
