package org.snapscript.studio.index.tree;

import static org.snapscript.studio.index.IndexType.SUPER;

import org.snapscript.core.Compilation;
import org.snapscript.core.module.Module;
import org.snapscript.core.module.Path;
import org.snapscript.core.scope.Scope;
import org.snapscript.core.variable.Value;
import org.snapscript.studio.index.IndexResult;
import org.snapscript.tree.define.TypeHierarchy;
import org.snapscript.tree.reference.TraitReference;
import org.snapscript.tree.reference.TypeReference;

public class TypeHierarchyIndex implements Compilation {
   
   private final TypeHierarchy hierarchy;
   private final TypeReference name;
   
   public TypeHierarchyIndex(TraitReference... traits) {
      this(null, traits);     
   }
   
   public TypeHierarchyIndex(TypeReference name, TraitReference... traits) {
      this.hierarchy = new TypeHierarchy(name, traits);
      this.name = name;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      if(name != null) {
         Scope scope = module.getScope();
         Value value = name.evaluate(scope, null);
         Object object = value.getValue();
         String type = String.valueOf(object);
         String prefix = module.getName();
         
         return new IndexResult(SUPER, hierarchy, null, prefix, type, path, line);
      }
      return hierarchy;
   }
}
