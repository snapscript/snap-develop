package org.snapscript.studio.index.tree;

import static org.snapscript.studio.index.IndexType.CLASS;

import org.snapscript.core.Compilation;
import org.snapscript.core.module.Module;
import org.snapscript.core.module.Path;
import org.snapscript.core.scope.Scope;
import org.snapscript.core.type.TypePart;
import org.snapscript.studio.index.IndexResult;
import org.snapscript.tree.annotation.AnnotationList;
import org.snapscript.tree.define.ClassDefinition;
import org.snapscript.tree.define.ClassName;
import org.snapscript.tree.define.TypeHierarchy;
import org.snapscript.tree.define.TypeName;

public class ClassDefinitionIndex implements Compilation {
   
   private final ClassDefinition definition;
   private final TypeName identifier;
   
   public ClassDefinitionIndex(AnnotationList annotations, ClassName name, TypeHierarchy hierarchy, TypePart... parts) {
      this.definition = new ClassDefinition(annotations, name, hierarchy, parts);
      this.identifier = name;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Scope scope = module.getScope();
      String name = identifier.getName(scope);
      String prefix = module.getName();
      
      return new IndexResult(CLASS, definition, null, prefix, name, path, line);
   }
}
