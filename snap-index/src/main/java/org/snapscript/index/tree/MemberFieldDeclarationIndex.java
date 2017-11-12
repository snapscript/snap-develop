package org.snapscript.index.tree;

import static org.snapscript.index.IndexType.PROPERTY;

import org.snapscript.core.Compilation;
import org.snapscript.core.Evaluation;
import org.snapscript.core.Module;
import org.snapscript.core.Path;
import org.snapscript.core.Scope;
import org.snapscript.core.Value;
import org.snapscript.index.IndexResult;
import org.snapscript.tree.constraint.Constraint;
import org.snapscript.tree.define.MemberFieldDeclaration;
import org.snapscript.tree.literal.TextLiteral;

public class MemberFieldDeclarationIndex implements Compilation {
   
   private final MemberFieldDeclaration declaration;
   private final TextLiteral identifier;
   private final Constraint constraint;
   
   public MemberFieldDeclarationIndex(TextLiteral identifier) {
      this(identifier, null, null);
   }
   
   public MemberFieldDeclarationIndex(TextLiteral identifier, Constraint constraint) {      
      this(identifier, constraint, null);
   }
   
   public MemberFieldDeclarationIndex(TextLiteral identifier, Evaluation value) {
      this(identifier, null, value);
   }
   
   public MemberFieldDeclarationIndex(TextLiteral identifier, Constraint constraint, Evaluation value) {
      this.declaration = new MemberFieldDeclaration(identifier, constraint, value);
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
         type = constraint.evaluate(scope, null).getValue();
      }
      return new IndexResult(PROPERTY, declaration, type, prefix, name, path, line);
   }
}
