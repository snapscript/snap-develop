package org.snapscript.studio.index.tree;

import static org.snapscript.studio.index.IndexType.SUPER;

import org.snapscript.core.Compilation;
import org.snapscript.core.module.Module;
import org.snapscript.core.module.Path;
import org.snapscript.core.scope.Scope;
import org.snapscript.core.variable.Value;
import org.snapscript.studio.index.IndexResult;
import org.snapscript.tree.reference.TypeNavigation;
import org.snapscript.tree.reference.TypeReference;

public class TraitReferenceIndex implements Compilation {
   
   private final TypeReference reference;

   public TraitReferenceIndex(TypeNavigation root, TypeNavigation... list) {
      this.reference = new TypeReference(root, list);
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Scope scope = module.getScope();
      Value value = reference.evaluate(scope, null);
      Object object = value.getValue();
      String type = String.valueOf(object);
      String prefix = module.getName();
      
      return new IndexResult(SUPER, reference, null, prefix, type, path, line);
   }
}
