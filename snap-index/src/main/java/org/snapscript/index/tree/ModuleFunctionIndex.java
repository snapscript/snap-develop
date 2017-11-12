package org.snapscript.index.tree;

import static org.snapscript.index.IndexType.MEMBER_FUNCTION;

import org.snapscript.core.Compilation;
import org.snapscript.core.Evaluation;
import org.snapscript.core.Module;
import org.snapscript.core.Path;
import org.snapscript.core.Scope;
import org.snapscript.core.Statement;
import org.snapscript.core.Value;
import org.snapscript.index.IndexResult;
import org.snapscript.tree.ModifierList;
import org.snapscript.tree.annotation.AnnotationList;
import org.snapscript.tree.constraint.Constraint;
import org.snapscript.tree.define.ModuleFunction;
import org.snapscript.tree.function.ParameterList;

public class ModuleFunctionIndex implements Compilation {
   
   private final ParameterList parameters;
   private final ModuleFunction function;
   private final Evaluation identifier;
   private final Constraint constraint;

   public ModuleFunctionIndex(AnnotationList annotations, ModifierList modifiers, Evaluation identifier, ParameterList parameters, Statement body){  
      this(annotations, modifiers, identifier, parameters, null, body);
   }
   
   public ModuleFunctionIndex(AnnotationList annotations, ModifierList modifiers, Evaluation identifier, ParameterList parameters, Constraint constraint, Statement body){
      this.function = new ModuleFunction(annotations, modifiers, identifier, parameters, constraint, body);
      this.parameters = parameters;
      this.identifier = identifier;
      this.constraint = constraint;
   }
   
   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Scope scope = module.getScope();
      Value value = identifier.evaluate(scope, null);
      String name = value.getString();
      String prefix = module.getName();
      String type = null;
      
      if(parameters != null) {
         name = name + parameters.create(scope);
      }
      if(constraint != null) {
         type = constraint.evaluate(scope, null).getValue();
      }
      return new IndexResult(MEMBER_FUNCTION, function, type, prefix, name, path, line);
   }
}
