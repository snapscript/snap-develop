package org.snapscript.index.tree;

import org.snapscript.core.Compilation;
import org.snapscript.core.Evaluation;
import org.snapscript.core.Module;
import org.snapscript.core.Path;
import org.snapscript.core.Scope;
import org.snapscript.core.Value;
import org.snapscript.index.IndexResult;
import org.snapscript.index.IndexType;
import org.snapscript.tree.Declaration;
import org.snapscript.tree.constraint.Constraint;
import org.snapscript.tree.literal.TextLiteral;

public class DeclarationIndex implements Compilation {
   
   private final TextLiteral identifier;
   private final Declaration declaration;
   
   public DeclarationIndex(TextLiteral identifier) {
      this(identifier, null, null);
   }
   
   public DeclarationIndex(TextLiteral identifier, Constraint constraint) {      
      this(identifier, constraint, null);
   }
   
   public DeclarationIndex(TextLiteral identifier, Evaluation value) {
      this(identifier, null, value);
   }
   
   public DeclarationIndex(TextLiteral identifier, Constraint constraint, Evaluation value) {
      this.declaration = new Declaration(identifier, constraint, value);
      this.identifier = identifier;
   } 

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Scope scope = module.getScope();
      Value value = identifier.evaluate(scope, null);
      String name = value.getString();
      
      return new IndexResult(IndexType.VARIABLE, declaration, name, path, line);
   }
}
