package org.snapscript.studio.index.tree;

import org.snapscript.core.Compilation;
import org.snapscript.core.Evaluation;
import org.snapscript.core.constraint.Constraint;
import org.snapscript.core.module.Module;
import org.snapscript.core.module.Path;
import org.snapscript.core.scope.Scope;
import org.snapscript.core.variable.Value;

public class ArrayConstraintIndex implements Compilation {
   
   private static final String[] DIMENSIONS = {"", "[]", "[][]", "[][][]", "[][][][]" };   

   private final IndexConstraint constraint;
   
   public ArrayConstraintIndex(Constraint entry, String name, int bounds) {
      this(entry, name, bounds, 0);
   }
   
   public ArrayConstraintIndex(Constraint entry, String name, int bounds, int modifiers) {
      this.constraint = new IndexConstraint(entry, bounds);
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      return constraint;
   }
   
   private static class IndexConstraint extends Evaluation {
      
      private final Constraint entry;
      private final int bounds;
      
      public IndexConstraint(Constraint entry, int bounds) {
         this.bounds = bounds;
         this.entry = entry;
      }

      @Override
      public Value evaluate(Scope scope, Value left) throws Exception {
         String type = entry.getName(scope);
         String name = type + DIMENSIONS[bounds];
         Module module = scope.getModule();
         
         return Value.getTransient(name, module);
      }
   }
}
