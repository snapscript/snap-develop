package org.snapscript.studio.index.tree;

import static org.snapscript.studio.index.IndexType.IMPORT;

import org.snapscript.core.Compilation;
import org.snapscript.core.Module;
import org.snapscript.core.NoStatement;
import org.snapscript.core.Path;
import org.snapscript.core.Statement;
import org.snapscript.studio.index.IndexResult;
import org.snapscript.tree.Qualifier;

public class ImportStaticIndex implements Compilation {
   
   private final Qualifier qualifier;
   private final Statement statement;

   public ImportStaticIndex(Qualifier qualifier) {
      this.statement = new NoStatement();
      this.qualifier = qualifier;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      String name = qualifier.getTarget();
      String fullName = qualifier.getQualifier();
      
      return new IndexResult(IMPORT, statement, null, fullName, name, path, line);
   }
}