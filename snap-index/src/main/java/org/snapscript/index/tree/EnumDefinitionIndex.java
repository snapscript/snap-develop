package org.snapscript.index.tree;

import org.snapscript.core.Compilation;
import org.snapscript.core.Module;
import org.snapscript.core.Path;
import org.snapscript.core.Scope;
import org.snapscript.index.IndexResult;
import org.snapscript.index.IndexType;
import org.snapscript.tree.annotation.AnnotationList;
import org.snapscript.tree.define.EnumDefinition;
import org.snapscript.tree.define.EnumList;
import org.snapscript.tree.define.TypeHierarchy;
import org.snapscript.tree.define.TypeName;
import org.snapscript.tree.define.TypePart;

public class EnumDefinitionIndex implements Compilation {
   
   private final EnumDefinition definition;
   private final TypeName identifier;
   
   public EnumDefinitionIndex(AnnotationList annotations, TypeName name, TypeHierarchy hierarchy, EnumList list, TypePart... parts) {
      this.definition = new EnumDefinition(annotations, name, hierarchy, list, parts);
      this.identifier = name;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Scope scope = module.getScope();
      String name = identifier.getName(scope);
      
      return new IndexResult(IndexType.ENUM, definition, null, name, path, line);
   }
}
