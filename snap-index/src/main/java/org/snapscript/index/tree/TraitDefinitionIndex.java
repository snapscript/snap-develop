package org.snapscript.index.tree;

import org.snapscript.core.Compilation;
import org.snapscript.core.Module;
import org.snapscript.core.Path;
import org.snapscript.core.Scope;
import org.snapscript.index.IndexResult;
import org.snapscript.index.IndexType;
import org.snapscript.tree.annotation.AnnotationList;
import org.snapscript.tree.define.TraitDefinition;
import org.snapscript.tree.define.TraitName;
import org.snapscript.tree.define.TypeHierarchy;
import org.snapscript.tree.define.TypePart;

public class TraitDefinitionIndex implements Compilation {
   
   private final TraitDefinition definition;
   private final TraitName identifier;
   
   public TraitDefinitionIndex(AnnotationList annotations, TraitName name, TypeHierarchy hierarchy, TypePart... parts) {
      this.definition = new TraitDefinition(annotations, name, hierarchy, parts);
      this.identifier = name;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Scope scope = module.getScope();
      String name = identifier.getName(scope);
      String prefix = module.getName();
      
      return new IndexResult(IndexType.TRAIT, definition, null, prefix, name, path, line);
   }
}
