package org.snapscript.develop.complete;

import java.util.Set;

import org.snapscript.develop.common.TypeNode;

public class UserExpression {
   
   private final SourceContext context;
   private final TypeNode constraint;
   private final Set<String> tokens;
   private final String complete;
   
   public UserExpression(String complete, TypeNode constraint, SourceContext context, Set<String> tokens) {
      this.constraint = constraint;
      this.context = context;
      this.complete = complete;
      this.tokens = tokens;
   }
   
   public SourceContext getContext(){
      return context;
   }
   
   public TypeNode getConstraint() {
      return constraint;
   }
   
   public Set<String> getTypes() {
      return tokens;
   }
   
   public String getExpression() {
      return complete;
   }
   
   @Override
   public String toString() {
      return String.format("%s -> %s", complete, constraint);
   }

}