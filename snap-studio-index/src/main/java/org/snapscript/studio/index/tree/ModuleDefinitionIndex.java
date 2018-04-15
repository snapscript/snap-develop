package org.snapscript.studio.index.tree;

import org.snapscript.core.Compilation;
import org.snapscript.core.module.Module;
import org.snapscript.core.module.Path;
import org.snapscript.core.scope.Scope;
import org.snapscript.core.variable.Value;
import org.snapscript.studio.index.IndexResult;
import org.snapscript.studio.index.IndexType;
import org.snapscript.tree.annotation.AnnotationList;
import org.snapscript.tree.define.ModuleDefinition;
import org.snapscript.tree.define.ModuleName;
import org.snapscript.tree.define.ModulePart;

public class ModuleDefinitionIndex implements Compilation {
   
   private final ModuleDefinition definition;
   private final ModuleName identifier;
   
   public ModuleDefinitionIndex(AnnotationList annotations, ModuleName module, ModulePart... body) {
      this.definition = new ModuleDefinition(annotations, module, body);
      this.identifier = module;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Scope scope = module.getScope();
      Value value = identifier.evaluate(scope, null);
      String name = value.getString();
      String prefix = module.getName();
      
      return new IndexResult(IndexType.MODULE, definition, null, prefix, name, path, line);
   }
}
