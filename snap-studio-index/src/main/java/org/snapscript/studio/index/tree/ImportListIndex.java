package org.snapscript.studio.index.tree;

import org.snapscript.core.Compilation;
import org.snapscript.core.NoStatement;
import org.snapscript.core.Statement;
import org.snapscript.core.module.Module;
import org.snapscript.core.module.Path;
import org.snapscript.core.scope.Scope;
import org.snapscript.core.variable.Value;
import org.snapscript.studio.index.IndexResult;
import org.snapscript.tree.Qualifier;

import static org.snapscript.studio.index.IndexType.IMPORT;

public class ImportListIndex implements Compilation {

   private final Qualifier qualifier;
   private final Qualifier[] names;
   private final IndexResult[] results;
   private final Statement statement;

   public ImportListIndex(Qualifier qualifier, Qualifier... names) {
      this.statement = new NoStatement();
      this.results = new IndexResult[names.length];
      this.qualifier = qualifier;
      this.names = names;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      String prefix = qualifier.getQualifier();

      for(int i = 0; i < names.length; i++) {
         String name = names[i].getQualifier();
         results[i] = new IndexResult(IMPORT, statement, null, prefix + "." + name, name, path, line);
      }
      return results;
   }
}
