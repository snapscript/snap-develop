package org.snapscript.develop;

import java.io.File;

import junit.framework.TestCase;

import org.snapscript.common.store.FileStore;
import org.snapscript.common.store.Store;
import org.snapscript.compile.Compiler;
import org.snapscript.compile.Executable;
import org.snapscript.compile.ResourceCompiler;
import org.snapscript.compile.StoreContext;
import org.snapscript.core.Context;

public class UnitTestSuiteTest extends TestCase {
   
   public void testSuite() throws Exception {
      String variable = System.getenv("PROJECT_ROOT");
      
      if(variable == null) {
         throw new IllegalStateException("Environment variable PROJECT_ROOT not set");
      }
      File root = new File(variable, "test");
      Store store = new FileStore(root);
      Context context = new StoreContext(store);
      Compiler compiler = new ResourceCompiler(context);
      Executable executable = compiler.compile("run.snap");

      executable.execute();
   }

}
