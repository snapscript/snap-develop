package org.snapscript.index.tree;

import static org.snapscript.index.IndexType.COMPOUND;

import org.snapscript.core.Compilation;
import org.snapscript.core.Module;
import org.snapscript.core.Path;
import org.snapscript.core.Statement;
import org.snapscript.index.IndexResult;
import org.snapscript.tree.CompoundStatement;

public class CompoundStatementIndex implements Compilation {

   private final CompoundStatement statement;
   
   public CompoundStatementIndex(Statement... statements) {
      this.statement = new CompoundStatement(statements);
   }
   
   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Object result = statement.compile(module, path, line);
      String prefix = module.getName();
      
      return new IndexResult(COMPOUND, result, null, prefix, "", path, line);
   }

}
