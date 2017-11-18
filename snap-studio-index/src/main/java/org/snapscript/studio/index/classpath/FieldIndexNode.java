package org.snapscript.studio.index.classpath;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;

import org.snapscript.studio.index.IndexNode;
import org.snapscript.studio.index.IndexType;

public class FieldIndexNode implements IndexNode {

   private Field field;
   
   public FieldIndexNode(Field field) {
      this.field = field;
   }
   
   @Override
   public int getLine() {
      return -1;
   }
   
   @Override
   public String getModule() {
      return getParent().getModule();
   }
   
   @Override
   public String getResource(){
      return null;
   }
   
   @Override
   public String getAbsolutePath(){
      return null;
   } 
    
   @Override
   public String getName() {
      return field.getName();
   }

   @Override
   public String getTypeName() {
      return getName();
   }

   @Override
   public String getFullName() {
      return getName();
   }

   @Override
   public IndexNode getConstraint() {
      Class type = field.getType();
      return ClassIndexProcessor.getIndexNode(type);
   }

   @Override
   public IndexNode getParent() {
      Class parent = field.getDeclaringClass();
      return ClassIndexProcessor.getIndexNode(parent);
   }

   @Override
   public IndexType getType() {
      return IndexType.PROPERTY;
   }

   @Override
   public Set<IndexNode> getNodes() {
      return Collections.emptySet();
   }
}