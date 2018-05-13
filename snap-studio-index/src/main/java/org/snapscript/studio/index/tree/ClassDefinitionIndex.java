package org.snapscript.studio.index.tree;

import org.snapscript.core.Compilation;
import org.snapscript.core.Statement;
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

import static org.snapscript.studio.index.IndexType.CLASS;

public class ClassDefinitionIndex implements Compilation {
   
   private final ClassDefinition definition;
   private final TypeName identifier;
   
   public ClassDefinitionIndex(AnnotationList annotations, ClassName name, TypeHierarchy hierarchy, TypePart... parts) {
      this.definition = new ClassDefinition(annotations, name, hierarchy, parts);
      this.identifier = name;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Statement statement = definition.compile(module, path, line);
      Scope scope = module.getScope();
      String name = identifier.getName(scope);
      String prefix = module.getName();
      
      return new IndexResult(CLASS, statement, null, prefix, name, path, line);
   }
}
