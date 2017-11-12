package org.snapscript.index.tree;

import org.snapscript.core.Compilation;
import org.snapscript.core.Module;
import org.snapscript.core.Path;
import org.snapscript.core.Reserved;
import org.snapscript.core.Statement;
import org.snapscript.index.IndexResult;
import org.snapscript.index.IndexType;
import org.snapscript.tree.ModifierList;
import org.snapscript.tree.annotation.AnnotationList;
import org.snapscript.tree.define.ClassConstructor;
import org.snapscript.tree.define.EnumConstructor;
import org.snapscript.tree.function.ParameterList;

public class EnumConstructorIndex implements Compilation {

   private final ClassConstructor constructor;
   
   public EnumConstructorIndex(AnnotationList annotations, ModifierList modifiers, ParameterList parameters, Statement body){  
      this.constructor = new EnumConstructor(annotations, modifiers, parameters, body);
   }
   
   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      return new IndexResult(IndexType.CONSTRUCTOR, constructor, null, Reserved.TYPE_CONSTRUCTOR, path, line);
   }
}