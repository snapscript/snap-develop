package org.snapscript.index.tree;

import org.snapscript.core.Compilation;
import org.snapscript.core.Evaluation;
import org.snapscript.core.Module;
import org.snapscript.core.NoStatement;
import org.snapscript.core.Path;
import org.snapscript.core.Statement;
import org.snapscript.index.IndexResult;
import org.snapscript.index.IndexType;
import org.snapscript.tree.Qualifier;

public class ImportIndex implements Compilation {
   
   private final Qualifier qualifier;
   private final Statement statement;

   public ImportIndex(Qualifier qualifier) {
      this(qualifier, null);
   }
   
   public ImportIndex(Qualifier qualifier, Evaluation alias) {
      this.statement = new NoStatement();
      this.qualifier = qualifier;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      String name = qualifier.getQualifier();
      return new IndexResult(IndexType.IMPORT, statement, null, name, path, line);
   }
}
