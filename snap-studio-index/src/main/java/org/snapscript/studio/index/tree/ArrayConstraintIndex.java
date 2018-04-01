package org.snapscript.studio.index.tree;

import org.snapscript.core.Bug;
import org.snapscript.core.Compilation;
import org.snapscript.core.error.InternalStateException;
import org.snapscript.core.module.Module;
import org.snapscript.core.module.Path;
import org.snapscript.core.scope.Scope;
import org.snapscript.core.scope.Value;
import org.snapscript.core.type.Type;
import org.snapscript.parse.StringToken;
import org.snapscript.tree.constraint.ArrayConstraint;
import org.snapscript.tree.reference.TypeReference;

public class ArrayConstraintIndex implements Compilation {
   
   private static final String[] DIMENSIONS = {"", "[]", "[][]", "[][][]", "[][][][]" };   

   private final IndexConstraint constraint;
   
   public ArrayConstraintIndex(TypeReference reference, StringToken... bounds) {
      this.constraint = new IndexConstraint(reference, bounds);
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      return constraint;
   }
   
   private static class IndexConstraint extends ArrayConstraint {
      
      private final TypeReference reference;
      private final StringToken[] bounds;
      
      public IndexConstraint(TypeReference reference, StringToken... bounds) {
         super(reference, bounds);
         this.reference = reference;
         this.bounds = bounds;
      }
   
      @Bug("fix this")
      @Override
      public Type getType(Scope scope) {
         try {
            Value value = reference.evaluate(scope, null);
            String entry = value.getValue();
            
            //return entry + DIMENSIONS[bounds.length];
            return null;
         } catch(Exception e) {
            throw new InternalStateException("Invalid array constraint", e);
         }
      }
   }
}
