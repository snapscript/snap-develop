package org.snapscript.index.tree;

import org.snapscript.core.Compilation;
import org.snapscript.core.Module;
import org.snapscript.core.Path;
import org.snapscript.core.Scope;
import org.snapscript.index.IndexResult;
import org.snapscript.index.IndexType;
import org.snapscript.tree.annotation.AnnotationList;
import org.snapscript.tree.define.ClassDefinition;
import org.snapscript.tree.define.TypeHierarchy;
import org.snapscript.tree.define.TypeName;
import org.snapscript.tree.define.TypePart;

public class ClassDefinitionIndex implements Compilation {
   
   private final ClassDefinition definition;
   private final TypeName identifier;
   
   public ClassDefinitionIndex(AnnotationList annotations, TypeName name, TypeHierarchy hierarchy, TypePart... parts) {
      this.definition = new ClassDefinition(annotations, name, hierarchy, parts);
      this.identifier = name;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Scope scope = module.getScope();
      String name = identifier.getName(scope);
      
      return new IndexResult(IndexType.CLASS, definition, name, path, line);
   }
}
