package org.snapscript.studio.index.tree;

import static org.snapscript.core.type.Category.CLASS;

import org.snapscript.core.Compilation;
import org.snapscript.core.Evaluation;
import org.snapscript.core.Identity;
import org.snapscript.core.InternalStateException;
import org.snapscript.core.constraint.Constraint;
import org.snapscript.core.module.Module;
import org.snapscript.core.module.Path;
import org.snapscript.core.scope.Scope;
import org.snapscript.core.type.Type;
import org.snapscript.core.type.index.ScopeType;
import org.snapscript.core.variable.Value;
import org.snapscript.tree.reference.GenericArgumentList;

public class GenericReferenceIndex implements Compilation {
   
   private final GenericArgumentList list;
   private final Evaluation evaluation;
   
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
      return new Identity(constraint, constraint);
   }   
   
   private static class IndexConstraint extends Constraint {
      
      private final Evaluation reference;
      
      public IndexConstraint(Evaluation reference) {
         this.reference = reference;
      }

      @Override
      public Type getType(Scope scope) {
         try {
            Value value = reference.evaluate(scope, null);
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
         super(module,null, CLASS, name, 0);
      }
      
      @Override
      public String toString() {
         return getName();
      }
      
   }

}
