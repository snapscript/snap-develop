package org.snapscript.studio.index;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

import org.snapscript.core.Reserved;

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
   
   public String getName() {
      IndexType type = index.getType();
      String name = index.getName();
      
      if(type.isConstructor()) {
         IndexNode parentNode = parent.get();
         String parentName = parentNode.getTypeName();
         
         return name.replace(Reserved.TYPE_CONSTRUCTOR + "(", parentName + "(");
      }
      return name;
   }
   
   public String getTypeName() {
      IndexType type = index.getType();
      String name = index.getName();

      if(type.isType()) {
         IndexNode parentNode = parent.get();
         IndexType parentType = parentNode.getType();
         
         if(parentType.isType() && !type.isRoot() && !type.isLeaf()) {
            return parentNode.getTypeName() + "." + name;
         }
         return name;
      }
      return name;
   }
   
   public String getFullName() {
      IndexType type = index.getType();
      String name = index.getName();
      
      if(type.isImport()) {
         return index.getModule();
      }
      if(type.isType()) {
         IndexNode parentNode = parent.get();
         IndexType parentType = parentNode.getType();
         
         if(parentType.isType() && !type.isRoot() && !type.isLeaf()) {
            return parentNode.getFullName() + "." + name;
         }
         String module = index.getModule();
         
         if(!module.endsWith(name)) {
            return module + "." + name;
         }
         return module;
      }
      return name;
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
