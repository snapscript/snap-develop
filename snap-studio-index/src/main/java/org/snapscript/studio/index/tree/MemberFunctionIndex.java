package org.snapscript.studio.index.tree;

import static org.snapscript.studio.index.IndexType.MEMBER_FUNCTION;

import org.snapscript.core.Compilation;
import org.snapscript.core.Evaluation;
import org.snapscript.core.Statement;
import org.snapscript.core.module.Module;
import org.snapscript.core.module.Path;
import org.snapscript.core.scope.Scope;
import org.snapscript.core.type.Type;
import org.snapscript.core.variable.Value;
import org.snapscript.core.constraint.Constraint;
import org.snapscript.studio.index.IndexResult;
import org.snapscript.tree.ModifierList;
import org.snapscript.tree.annotation.AnnotationList;
import org.snapscript.tree.define.MemberFunction;
import org.snapscript.tree.function.ParameterList;

public class MemberFunctionIndex implements Compilation {
   
   private final ParameterList parameters;
   private final MemberFunction function;
   private final Evaluation identifier;
   private final Constraint constraint;
   
   public MemberFunctionIndex(AnnotationList annotations, ModifierList modifiers, Evaluation identifier, ParameterList parameters){
      this(annotations, modifiers, identifier, parameters, null, null);
   }
   
   public MemberFunctionIndex(AnnotationList annotations, ModifierList modifiers, Evaluation identifier, ParameterList parameters, Constraint constraint){
      this(annotations, modifiers, identifier, parameters, constraint, null);
   }
   
   public MemberFunctionIndex(AnnotationList annotations, ModifierList modifiers, Evaluation identifier, ParameterList parameters, Statement body){  
      this(annotations, modifiers, identifier, parameters, null, body);
   }
   
   public MemberFunctionIndex(AnnotationList annotations, ModifierList modifiers, Evaluation identifier, ParameterList parameters, Constraint constraint, Statement body){ 
      this.function = new MemberFunction(annotations, modifiers, identifier, parameters, constraint, body);
      this.constraint = constraint;
      this.identifier = identifier;
      this.parameters = parameters;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Scope scope = module.getScope();
      Value value = identifier.evaluate(scope, null);
      String name = value.getData().getString();
      String prefix = module.getName(); // this is not the correct package
      String type = null;
      
      if(parameters != null) {
         name = name + parameters.create(scope);
      }
      if(constraint != null) {
         Type object = constraint.getType(scope);
         type = String.valueOf(object);
      }
      return new IndexResult(MEMBER_FUNCTION, function, type, prefix, name, path, line);
   }
}
