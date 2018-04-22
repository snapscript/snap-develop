package org.snapscript.studio.index.tree;

import static org.snapscript.studio.index.IndexType.SUPER;

import org.snapscript.core.Compilation;
import org.snapscript.core.constraint.Constraint;
import org.snapscript.core.module.Module;
import org.snapscript.core.module.Path;
import org.snapscript.core.scope.Scope;
import org.snapscript.core.type.Type;
import org.snapscript.studio.index.IndexResult;
import org.snapscript.tree.constraint.TraitConstraint;

public class TraitConstraintIndex implements Compilation {
   
   private final TraitConstraint constraint;

   public TraitConstraintIndex(Constraint constraint) {
      this.constraint = new TraitConstraint(constraint);
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Scope scope = module.getScope();
      Type type = constraint.getType(scope);
      String name = String.valueOf(type);
      String prefix = module.getName();
      
      return new IndexResult(SUPER, constraint, null, prefix, name, path, line);
   }
}
