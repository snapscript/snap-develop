package org.snapscript.studio.index.tree;

import static org.snapscript.studio.index.IndexType.PARAMETER;

import org.snapscript.core.Compilation;
import org.snapscript.core.Evaluation;
import org.snapscript.core.Module;
import org.snapscript.core.Path;
import org.snapscript.core.Scope;
import org.snapscript.core.Value;
import org.snapscript.core.function.Parameter;
import org.snapscript.studio.index.IndexResult;
import org.snapscript.tree.Modifier;
import org.snapscript.tree.NameReference;
import org.snapscript.tree.annotation.AnnotationList;
import org.snapscript.tree.constraint.Constraint;
import org.snapscript.tree.function.ParameterDeclaration;

public class ParameterDeclarationIndex implements Compilation  {
   
   private final ParameterDeclaration declaration;
   private final Evaluation identifier;
   private final Constraint constraint;
   
   public ParameterDeclarationIndex(AnnotationList annotations, Evaluation identifier){
      this(annotations, identifier, null, null);
   }
   
   public ParameterDeclarationIndex(AnnotationList annotations, Evaluation identifier, Constraint constraint){
      this(annotations, identifier, null, constraint);
   }
   
   public ParameterDeclarationIndex(AnnotationList annotations, Evaluation identifier, Modifier modifier){
      this(annotations, identifier, modifier, null);
   }
   
   public ParameterDeclarationIndex(AnnotationList annotations, Evaluation identifier, Modifier modifier, Constraint constraint){
      this.declaration = new IndexParameterDeclaration(annotations, identifier, modifier, constraint);
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
      
      if(constraint != null) {
         Value result = constraint.evaluate(scope, null);
         Object object = result.getValue();
         
         type = String.valueOf(object);
      }
      return new IndexResult(PARAMETER, declaration, type, prefix, name, path, line);
   }
   
   private static class IndexParameterDeclaration extends ParameterDeclaration {
      
      private final NameReference reference;
      private final Modifier modifier;
      
      public IndexParameterDeclaration(AnnotationList annotations, Evaluation identifier, Modifier modifier, Constraint constraint){
         super(annotations, identifier, modifier, constraint);
         this.reference = new NameReference(identifier);
         this.modifier = modifier;
      }

      @Override
      public Parameter get(Scope scope) throws Exception {
         String name = reference.getName(scope);
         return new Parameter(name, null, modifier != null);
      }
   }
}