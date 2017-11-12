package org.snapscript.index.tree;

import org.snapscript.core.Compilation;
import org.snapscript.core.Module;
import org.snapscript.core.Path;
import org.snapscript.core.Statement;
import org.snapscript.index.IndexResult;
import org.snapscript.index.IndexType;
import org.snapscript.tree.script.Script;

public class ScriptIndex implements Compilation {

   private final Script script;
      
   public ScriptIndex(Statement... statements) {
      this.script = new Script(statements);
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      String name = path.getPath();
      return new IndexResult(IndexType.SCRIPT, script, null, name, path, line);
   }
}
