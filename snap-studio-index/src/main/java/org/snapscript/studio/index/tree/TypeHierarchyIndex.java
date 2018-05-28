package org.snapscript.studio.index.tree;

import static org.snapscript.studio.index.IndexType.SUPER;

import org.snapscript.core.Compilation;
import org.snapscript.core.module.Module;
import org.snapscript.core.module.Path;
import org.snapscript.core.scope.Scope;
import org.snapscript.core.type.Type;
import org.snapscript.studio.index.IndexResult;
import org.snapscript.tree.constraint.ClassConstraint;
import org.snapscript.tree.constraint.TraitConstraint;
import org.snapscript.tree.define.ClassHierarchy;

public class TypeHierarchyIndex implements Compilation {
   
   private final ClassHierarchy hierarchy;
   private final ClassConstraint name;
   
   public TypeHierarchyIndex(TraitConstraint... traits) {
      this(null, traits);     
   }
   
   public TypeHierarchyIndex(ClassConstraint name, TraitConstraint... traits) {
      this.hierarchy = new ClassHierarchy(name, traits);
      this.name = name;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      if(name != null) {
         Scope scope = module.getScope();
         Type constraint = name.getType(scope);
         String type = String.valueOf(constraint);
         String prefix = module.getName();
         
         return new IndexResult(SUPER, hierarchy, null, prefix, type, path, line);
      }
      return hierarchy;
   }
}
