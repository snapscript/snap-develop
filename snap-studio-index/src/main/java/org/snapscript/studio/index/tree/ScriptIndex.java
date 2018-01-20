package org.snapscript.studio.index.tree;

import static org.snapscript.studio.index.IndexType.SCRIPT;

import org.snapscript.core.Compilation;
import org.snapscript.core.Module;
import org.snapscript.core.Path;
import org.snapscript.core.Statement;
import org.snapscript.studio.index.IndexResult;
import org.snapscript.tree.script.Script;

public class ScriptIndex implements Compilation {

   private final Script script;
      
   public ScriptIndex(Statement... statements) {
      this.script = new Script(statements);
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      String name = path.getPath();
      String prefix = module.getName();
      
      return new IndexResult(SCRIPT, script, null, prefix, name, path, line);
   }
}
