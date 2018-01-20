package org.snapscript.studio.index.tree;

import org.snapscript.core.Compilation;
import org.snapscript.core.Module;
import org.snapscript.core.Path;
import org.snapscript.core.Scope;
import org.snapscript.core.Value;
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
   
      @Override
      public Value evaluate(Scope scope, Object left) throws Exception {
         Value value = reference.evaluate(scope, null);
         String entry = value.getValue();
         String array = entry + DIMENSIONS[bounds.length];

         return Value.getTransient(array);
      }
   }
}
