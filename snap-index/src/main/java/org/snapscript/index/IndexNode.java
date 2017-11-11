package org.snapscript.index;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

public class IndexNode {

   private final AtomicReference<IndexNode> parent;
   private final Comparator<IndexNode> comparator;
   private final Set<IndexNode> nodes;
   private final Index index;
   
   public IndexNode(Index index) {
      this.parent = new AtomicReference<IndexNode>();
      this.comparator = new IndexNodeComparator();
      this.nodes = new TreeSet<IndexNode>(comparator);
      this.index = index;
   }
   
   public Index getIndex() {
      return index;
   }
   
   public IndexNode getParent() {
      return parent.get();
   }
   
   public void setParent(IndexNode node) {
      parent.set(node);
   }
   
   public IndexType getType() {
      return index.getType();
   }
   
   public Set<IndexType> getParentTypes() {
      return index.getType().getParentTypes();
   }
   
   public Set<IndexNode> getNodes() {
      return nodes;
   }
   
   @Override
   public String toString() {
      return index.toString();
   }
}
