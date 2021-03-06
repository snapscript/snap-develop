package org.snapscript.studio.index.tree;

import org.snapscript.core.Compilation;
import org.snapscript.core.Evaluation;
import org.snapscript.core.IdentityEvaluation;
import org.snapscript.core.constraint.Constraint;
import org.snapscript.core.error.InternalStateException;
import org.snapscript.core.module.Module;
import org.snapscript.core.module.Path;
import org.snapscript.core.scope.Scope;
import org.snapscript.core.type.Type;
import org.snapscript.core.type.index.ScopeType;
import org.snapscript.core.variable.Value;
import org.snapscript.parse.StringToken;
import org.snapscript.tree.literal.TextLiteral;
import org.snapscript.tree.reference.GenericArgumentList;

import static org.snapscript.core.ModifierType.CLASS;
import static org.snapscript.core.variable.Value.NULL;

public class GenericReferenceIndex implements Compilation {
   
   private final GenericArgumentList list;
   private final Evaluation evaluation;

   public GenericReferenceIndex(StringToken token) {
      this(new TextLiteral(token));
   }

   public GenericReferenceIndex(Evaluation evaluation) {
      this(evaluation, null);
   }

   public GenericReferenceIndex(Evaluation evaluation, GenericArgumentList list) {
      this.evaluation = evaluation;
      this.list = list;
   }

   @Override
   public Evaluation compile(Module module, Path path, int line) throws Exception {
      Constraint constraint = new IndexConstraint(evaluation);
      return new IdentityEvaluation(constraint, constraint);
   }   
   
   private static class IndexConstraint extends Constraint {
      
      private final Evaluation reference;
      
      public IndexConstraint(Evaluation reference) {
         this.reference = reference;
      }

      @Override
      public Type getType(Scope scope) {
         try {
            Value value = reference.evaluate(scope, NULL);
            Module module = scope.getModule();
            String entry = value.getValue();

            return new IndexType(module, entry);
         } catch(Exception e) {
            throw new InternalStateException("Invalid array constraint", e);
         }
      }
   }
   
   private static class IndexType extends ScopeType {

      public IndexType(Module module, String name) {
         super(module,null, name, CLASS.mask, 0);
      }
      
      @Override
      public String toString() {
         return getName();
      }
      
   }

}
