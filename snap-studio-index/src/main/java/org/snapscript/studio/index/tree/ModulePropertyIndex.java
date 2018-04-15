package org.snapscript.studio.index.tree;

import static org.snapscript.studio.index.IndexType.PROPERTY;

import org.snapscript.core.Compilation;
import org.snapscript.core.Evaluation;
import org.snapscript.core.module.Module;
import org.snapscript.core.module.Path;
import org.snapscript.core.scope.Scope;
import org.snapscript.core.type.Type;
import org.snapscript.core.variable.Value;
import org.snapscript.core.constraint.Constraint;
import org.snapscript.studio.index.IndexResult;
import org.snapscript.tree.define.ModuleProperty;
import org.snapscript.tree.literal.TextLiteral;

public class ModulePropertyIndex implements Compilation {

   private final ModuleProperty property;
   private final TextLiteral identifier;
   private final Constraint constraint;
   
   public ModulePropertyIndex(TextLiteral identifier) {
      this(identifier, null, null);
   }
   
   public ModulePropertyIndex(TextLiteral identifier, Constraint constraint) {      
      this(identifier, constraint, null);
   }
   
   public ModulePropertyIndex(TextLiteral identifier, Evaluation value) {
      this(identifier, null, value);
   }
   
   public ModulePropertyIndex(TextLiteral identifier, Constraint constraint, Evaluation value) {
      this.property = new ModuleProperty(identifier, constraint, value);
      this.constraint = constraint;
      this.identifier = identifier;
   }  
   
   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Scope scope = module.getScope();
      Value value = identifier.evaluate(scope, null);
      String name = value.getString();
      String prefix = module.getName();
      String type = null;
      
      if(constraint != null) {
         Type object = constraint.getType(scope);
         type = String.valueOf(object);
      }
      return new IndexResult(PROPERTY, property, type, prefix, name, path, line);
   }
}
