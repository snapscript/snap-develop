package org.snapscript.studio.index.tree;

import static org.snapscript.studio.index.IndexType.IMPORT;

import org.snapscript.core.Compilation;
import org.snapscript.core.Evaluation;
import org.snapscript.core.Module;
import org.snapscript.core.NoStatement;
import org.snapscript.core.Path;
import org.snapscript.core.Scope;
import org.snapscript.core.Statement;
import org.snapscript.core.Value;
import org.snapscript.studio.index.IndexResult;
import org.snapscript.tree.Qualifier;

public class ImportIndex implements Compilation {
   
   private final Qualifier qualifier;
   private final Statement statement;
   private final Evaluation alias;

   public ImportIndex(Qualifier qualifier) {
      this(qualifier, null);
   }
   
   public ImportIndex(Qualifier qualifier, Evaluation alias) {
      this.statement = new NoStatement();
      this.qualifier = qualifier;
      this.alias = alias;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      String name = qualifier.getTarget();
      String fullName = qualifier.getQualifier();
      
      if(alias != null) {
         Scope scope = module.getScope();
         Value value = alias.evaluate(scope, null);
         
         name = value.getString();
      }
      return new IndexResult(IMPORT, statement, null, fullName, name, path, line);
   }
}
