package org.snapscript.studio.index.tree;

import org.snapscript.core.Compilation;
import org.snapscript.core.Evaluation;
import org.snapscript.core.module.Module;
import org.snapscript.core.module.Path;
import org.snapscript.core.scope.Scope;
import org.snapscript.core.variable.Value;
import org.snapscript.tree.NameReference;
import org.snapscript.tree.reference.TypeNavigation;

public class TypeReferencePartIndex implements Compilation {

   private final NameReference reference;  
   
   public TypeReferencePartIndex(Evaluation type) {
      this.reference = new NameReference(type);
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Scope scope = module.getScope();
      String name = reference.getName(scope);
      
      return new TypeIndexPart(name);
   }
   
   private static class TypeIndexPart extends TypeNavigation {
      
      private final String name;
      
      public TypeIndexPart(String name) {
         this.name = name;
      }

      @Override
      public String qualify(Scope scope, String left) throws Exception {
         if(left != null) {
            return left + '$' +name;
         }
         return name;
      }
      
      @Override
      public Value evaluate(Scope scope, Value left) {
         Object object = left.getValue();
         
         if(object != null) {
            return Value.getTransient(object + "." + name);
         }
         return Value.getTransient(name);
      }
   }
}