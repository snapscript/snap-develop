package org.snapscript.studio.index.tree;

import static org.snapscript.studio.index.IndexType.ENUM;

import org.snapscript.core.Compilation;
import org.snapscript.core.Module;
import org.snapscript.core.Path;
import org.snapscript.core.Scope;
import org.snapscript.studio.index.IndexResult;
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
      String prefix = module.getName();
      
      return new IndexResult(ENUM, definition, null, prefix, name, path, line);
   }
}
