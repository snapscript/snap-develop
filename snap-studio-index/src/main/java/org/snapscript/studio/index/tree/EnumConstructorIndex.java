package org.snapscript.studio.index.tree;

import static org.snapscript.core.Reserved.TYPE_CONSTRUCTOR;
import static org.snapscript.studio.index.IndexType.CONSTRUCTOR;

import org.snapscript.core.Compilation;
import org.snapscript.core.Statement;
import org.snapscript.core.module.Module;
import org.snapscript.core.module.Path;
import org.snapscript.core.scope.Scope;
import org.snapscript.studio.index.IndexResult;
import org.snapscript.tree.ModifierList;
import org.snapscript.tree.annotation.AnnotationList;
import org.snapscript.tree.define.ClassConstructor;
import org.snapscript.tree.define.EnumConstructor;
import org.snapscript.tree.function.ParameterList;

public class EnumConstructorIndex implements Compilation {

   private final ClassConstructor constructor;
   private final ParameterList parameters;
   
   public EnumConstructorIndex(AnnotationList annotations, ModifierList modifiers, ParameterList parameters, Statement body){  
      this.constructor = new EnumConstructor(annotations, modifiers, parameters, body);
      this.parameters = parameters;
   }
   
   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Scope scope = module.getScope();
      String prefix = module.getName();
      String name = TYPE_CONSTRUCTOR;
      
      if(parameters != null) {
         name = name + parameters.create(scope);
      }
      return new IndexResult(CONSTRUCTOR, constructor, null, prefix, name, path, line);
   }
}