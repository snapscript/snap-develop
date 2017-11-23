package org.snapscript.studio.index.classpath;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Set;

import org.snapscript.studio.index.IndexNode;
import org.snapscript.studio.index.IndexType;

public class ConstructorIndexNode implements IndexNode {
   
   private static final String[] PREFIX = {
   "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", 
   "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
   
   private Constructor constructor;
   private String description;
   
   public ConstructorIndexNode(Constructor constructor) {
      this.constructor = constructor;
   }
   
   
   @Override
   public int getLine() {
      return -1;
   }
   
   @Override
   public boolean isNative(){
      return true;
   }
   
   @Override
   public boolean isPublic(){
      return constructor.isAccessible();
   }
   
   @Override
   public String getModule(){
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
      if(description == null) {
         Class[] types = constructor.getParameterTypes();
         String name = constructor.getDeclaringClass().getSimpleName();
         StringBuilder builder = new StringBuilder();
         
         builder.append(name);
         builder.append("(");
         
         for(int i = 0; i < types.length; i++) {
            String parameter = PREFIX[i];
            
            if(i > 0) {
               builder.append(", ");
            }
            builder.append(parameter);
         }
         builder.append(")");
         description = builder.toString();
      }
      return description;
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
      Class returnType = constructor.getDeclaringClass();
      return ClassIndexProcessor.getIndexNode(returnType);
   }

   @Override
   public IndexNode getParent() {
      Class parent = constructor.getDeclaringClass();
      return ClassIndexProcessor.getIndexNode(parent);
   }

   @Override
   public IndexType getType() {
      return IndexType.CONSTRUCTOR;
   }

   @Override
   public Set<IndexNode> getNodes() {
      return Collections.emptySet();
   }
}