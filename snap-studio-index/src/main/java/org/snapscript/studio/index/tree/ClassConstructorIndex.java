package org.snapscript.studio.index.tree;

import static org.snapscript.core.Reserved.TYPE_CONSTRUCTOR;
import static org.snapscript.studio.index.IndexType.CONSTRUCTOR;

import org.snapscript.core.Compilation;
import org.snapscript.core.Module;
import org.snapscript.core.Path;
import org.snapscript.core.Scope;
import org.snapscript.core.Statement;
import org.snapscript.studio.index.IndexResult;
import org.snapscript.tree.ModifierList;
import org.snapscript.tree.annotation.AnnotationList;
import org.snapscript.tree.define.ClassConstructor;
import org.snapscript.tree.define.TypePart;
import org.snapscript.tree.function.ParameterList;

public class ClassConstructorIndex implements Compilation {

   private final ClassConstructor constructor;
   private final ParameterList parameters;
   
   public ClassConstructorIndex(AnnotationList annotations, ModifierList modifiers, ParameterList parameters, Statement body){  
      this(annotations, modifiers, parameters, null, body);
   }  
   
   public ClassConstructorIndex(AnnotationList annotations, ModifierList modifiers, ParameterList parameters, TypePart part, Statement body){  
      this.constructor = new ClassConstructor(annotations, modifiers, parameters, part, body);
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

