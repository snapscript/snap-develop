package org.snapscript.studio.index.tree;

import static org.snapscript.studio.index.IndexType.SUPER;

import org.snapscript.core.Compilation;
import org.snapscript.core.module.Module;
import org.snapscript.core.module.Path;
import org.snapscript.core.scope.Scope;
import org.snapscript.core.type.Type;
import org.snapscript.studio.index.IndexResult;
import org.snapscript.tree.constraint.TraitConstraint;
import org.snapscript.tree.constraint.TypeConstraint;
import org.snapscript.tree.define.ClassHierarchy;
import org.snapscript.tree.define.TypeHierarchy;

public class TypeHierarchyIndex implements Compilation {
   
   private final ClassHierarchy hierarchy;
   private final TypeConstraint name;
   
   public TypeHierarchyIndex(TraitConstraint... traits) {
      this(null, traits);     
   }
   
   public TypeHierarchyIndex(TypeConstraint name, TraitConstraint... traits) {
      this.hierarchy = new ClassHierarchy(name, traits);
      this.name = name;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      TypeHierarchy result = hierarchy.compile(module, path, line);
      
      if(name != null) {
         Scope scope = module.getScope();
         Type constraint = name.getType(scope);
         String type = String.valueOf(constraint);
         String prefix = module.getName();
         
         return new IndexResult(SUPER, result, null, prefix, type, path, line);
      }
      return result;
   }
}
