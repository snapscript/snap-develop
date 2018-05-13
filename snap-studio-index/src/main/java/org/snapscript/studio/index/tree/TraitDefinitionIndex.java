package org.snapscript.studio.index.tree;

import org.snapscript.core.Compilation;
import org.snapscript.core.Statement;
import org.snapscript.core.module.Module;
import org.snapscript.core.module.Path;
import org.snapscript.core.scope.Scope;
import org.snapscript.core.type.TypePart;
import org.snapscript.studio.index.IndexResult;
import org.snapscript.studio.index.IndexType;
import org.snapscript.tree.annotation.AnnotationList;
import org.snapscript.tree.define.TraitDefinition;
import org.snapscript.tree.define.TraitName;
import org.snapscript.tree.define.TypeHierarchy;

public class TraitDefinitionIndex implements Compilation {
   
   private final TraitDefinition definition;
   private final TraitName identifier;
   
   public TraitDefinitionIndex(AnnotationList annotations, TraitName name, TypeHierarchy hierarchy, TypePart... parts) {
      this.definition = new TraitDefinition(annotations, name, hierarchy, parts);
      this.identifier = name;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Statement statement = definition.compile(module, path, line);
      Scope scope = module.getScope();
      String name = identifier.getName(scope);
      String prefix = module.getName();
      
      return new IndexResult(IndexType.TRAIT, statement, null, prefix, name, path, line);
   }
}
