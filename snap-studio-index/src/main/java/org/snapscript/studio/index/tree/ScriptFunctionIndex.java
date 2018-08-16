package org.snapscript.studio.index.tree;

import org.snapscript.core.Compilation;
import org.snapscript.core.Statement;
import org.snapscript.core.constraint.Constraint;
import org.snapscript.core.module.Module;
import org.snapscript.core.module.Path;
import org.snapscript.core.scope.Scope;
import org.snapscript.core.type.Type;
import org.snapscript.studio.index.IndexResult;
import org.snapscript.tree.constraint.FunctionName;
import org.snapscript.tree.function.ParameterList;
import org.snapscript.tree.script.ScriptFunction;

import static org.snapscript.studio.index.IndexType.FUNCTION;

public class ScriptFunctionIndex implements Compilation {
   
   private final ParameterList parameters;
   private final ScriptFunction function;
   private final FunctionName identifier;
   private final Constraint constraint;
   
   public ScriptFunctionIndex(FunctionName identifier, ParameterList parameters, Statement body){
      this(identifier, parameters, null, body);
   }
   
   public ScriptFunctionIndex(FunctionName identifier, ParameterList parameters, Constraint constraint, Statement body){
      this.function = new ScriptFunction(identifier, parameters, constraint, body);
      this.parameters = parameters;
      this.constraint = constraint;
      this.identifier = identifier;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Scope scope = module.getScope();
      String name = identifier.getName(scope);
      String prefix = module.getName();
      String type = null;
      
      if(parameters != null) {
         name = name + parameters.create(scope);
      }
      if(constraint != null) {
         Type object = constraint.getType(scope);
         type = String.valueOf(object);
      }
      return new IndexResult(FUNCTION, function, type, prefix, name, path, line);
   }  
}
