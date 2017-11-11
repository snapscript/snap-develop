package org.snapscript.index.tree;

import org.snapscript.core.Compilation;
import org.snapscript.core.Evaluation;
import org.snapscript.core.Module;
import org.snapscript.core.Path;
import org.snapscript.core.Scope;
import org.snapscript.core.Statement;
import org.snapscript.core.Value;
import org.snapscript.index.IndexResult;
import org.snapscript.index.IndexType;
import org.snapscript.tree.constraint.Constraint;
import org.snapscript.tree.function.ParameterList;
import org.snapscript.tree.script.ScriptFunction;

public class ScriptFunctionIndex implements Compilation {
   
   private final ScriptFunction function;
   private final Evaluation identifier;
   
   public ScriptFunctionIndex(Evaluation identifier, ParameterList parameters, Statement body){  
      this(identifier, parameters, null, body);
   }
   
   public ScriptFunctionIndex(Evaluation identifier, ParameterList parameters, Constraint constraint, Statement body){  
      this.function = new ScriptFunction(identifier, parameters, constraint, body);
      this.identifier = identifier;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Scope scope = module.getScope();
      Value value = identifier.evaluate(scope, null);
      String name = value.getString();
      
      return new IndexResult(IndexType.FUNCTION, function, name, path, line);
   }  
}
