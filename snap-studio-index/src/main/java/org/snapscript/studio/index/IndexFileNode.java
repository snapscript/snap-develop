package org.snapscript.studio.index;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

import org.snapscript.core.Reserved;

public class IndexFileNode implements IndexNode {

   private final AtomicReference<IndexNode> parent;
   private final Comparator<IndexNode> comparator;
   private final IndexDatabase database;
   private final Set<IndexNode> nodes;
   private final String resource;
   private final Index index;
   
   public IndexFileNode(IndexDatabase database, Index index, String resource) {
      this.parent = new AtomicReference<IndexNode>();
      this.comparator = new IndexNodeComparator();
      this.nodes = new TreeSet<IndexNode>(comparator);
      this.database = database;
      this.resource = resource;
      this.index = index;
   }
   
   @Override
   public int getLine() {
      return index.getLine();
   }
   
   @Override
   public String getResource() {
      return resource;
   }
   
   @Override
   public String getAbsolutePath() {
      return null;
   }
   
   @Override
   public String getModule() {
      return index.getModule();
   }
   
   @Override
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
   
   @Override
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
   
   @Override
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
   
   @Override
   public IndexNode getConstraint() {
      String constraint = index.getConstraint();
      String module = index.getModule();
      
      if(constraint != null) {
         Map<String, IndexNode> nodes = IndexSearcher.getNodesInScope(this);
         IndexNode node = nodes.get(constraint);
         
         if(node == null) {
            try {
               return database.getDefaultImport(module, constraint);
            } catch(Exception e) {
               return null;
            }
         } else {
            IndexType type = node.getType();
            
            if(type.isImport()) {
               String fullName = node.getFullName();
               
               try {
                  node = database.getTypeNode(fullName);
               } catch(Exception e) {               
               }
               try {
                  node = database.getDefaultImport(module, fullName);
               } catch(Exception e) {               
               }
            }
         }
         return node;
      }
      return null;
   }
   
   @Override
   public IndexNode getParent() {
      return parent.get();
   }
   
   public void setParent(IndexNode node) {
      parent.set(node);
   }
   
   @Override
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

