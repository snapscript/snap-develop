package org.snapscript.studio.index.tree;

import org.snapscript.core.Compilation;
import org.snapscript.core.Evaluation;
import org.snapscript.core.Module;
import org.snapscript.core.Path;
import org.snapscript.core.Scope;
import org.snapscript.core.Value;
import org.snapscript.tree.NameReference;

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
   
   private static class TypeIndexPart extends Evaluation {
      
      private final String name;
      
      public TypeIndexPart(String name) {
         this.name = name;
      }
      
      @Override
      public Value evaluate(Scope scope, Object left) {
         if(left != null) {
            return Value.getTransient(left + "." + name);
         }
         return Value.getTransient(name);
      }
   }
}