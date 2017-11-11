package org.snapscript.index.tree;

import org.snapscript.core.Compilation;
import org.snapscript.core.Module;
import org.snapscript.core.Path;
import org.snapscript.core.Scope;
import org.snapscript.core.Statement;
import org.snapscript.core.Value;
import org.snapscript.index.IndexResult;
import org.snapscript.index.IndexType;
import org.snapscript.tree.annotation.AnnotationList;
import org.snapscript.tree.define.ModuleDefinition;
import org.snapscript.tree.define.ModuleName;

public class ModuleDefinitionIndex implements Compilation {
   
   private final ModuleDefinition definition;
   private final ModuleName identifier;
   
   public ModuleDefinitionIndex(AnnotationList annotations, ModuleName module, Statement... body) {
      this.definition = new ModuleDefinition(annotations, module, body);
      this.identifier = module;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Scope scope = module.getScope();
      Value value = identifier.evaluate(scope, null);
      String name = value.getString();
      
      return new IndexResult(IndexType.MODULE, definition, name, path, line);
   }
}
