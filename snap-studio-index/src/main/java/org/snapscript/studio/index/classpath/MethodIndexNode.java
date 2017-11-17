package org.snapscript.studio.index.classpath;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;

import org.snapscript.studio.index.IndexNode;
import org.snapscript.studio.index.IndexType;

public class MethodIndexNode implements IndexNode {
   
   private static final String[] PREFIX = {
   "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", 
   "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
   
   private Method method;
   
   public MethodIndexNode( Method method) {
      this.method = method;
   }
   
   @Override
   public int getLine() {
      return -1;
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
      Class[] types = method.getParameterTypes();
      String name = method.getName();
      
      if(types.length > 0) {
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
         return builder.toString();
      }
      return name + "()";
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
      Class returnType = method.getReturnType();
      return ClassReflectionIndexer.getIndexNode(returnType);
   }

   @Override
   public IndexNode getParent() {
      Class parent = method.getDeclaringClass();
      return ClassReflectionIndexer.getIndexNode(parent);
   }

   @Override
   public IndexType getType() {
      return IndexType.MEMBER_FUNCTION;
   }

   @Override
   public Set<IndexNode> getNodes() {
      return Collections.emptySet();
   }
}