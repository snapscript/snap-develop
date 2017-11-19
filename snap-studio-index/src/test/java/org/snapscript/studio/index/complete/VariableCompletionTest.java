package org.snapscript.studio.index.complete;

import java.io.File;
import java.util.Map;

import junit.framework.TestCase;

import org.snapscript.common.store.ClassPathStore;
import org.snapscript.common.thread.ThreadPool;
import org.snapscript.compile.StoreContext;
import org.snapscript.core.Context;
import org.snapscript.studio.index.IndexDatabase;
import org.snapscript.studio.index.IndexScanner;

public class VariableCompletionTest extends TestCase {
   
   private static final String SOURCE =
   "class Point{\n"+
   "   var x: Integer;\n"+
   "   var y: Integer;\n"+
   "   new(x: Integer, y: Integer){\n"+
   "      this.x = x;\n"+
   "      this.y = y;\n"+
   "   }\n"+
   "   getX(){\n"+
   "      return x;\n"+
   "   }\n"+
   "   getY(){\n"+
   "      return y;\n"+
   "   }\n"+   
   "   override toString(){\n"+
   "      \"${x},${y}\"; // last expression evaluated returned\n"+
   "   }\n"+
   "}\n"+
   "var point: Point = new Point(null, 1);\n"+
   "var builder: StringBuilder = new StringBuilder();\n"+
   "\n";
   
   public void testVariableCompletion() throws Exception {
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      ThreadPool pool = new ThreadPool(2);
      File file = File.createTempFile("test", getClass().getSimpleName());
      IndexDatabase database = new IndexScanner(ClassLoader.getSystemClassLoader(), context, pool, file, "test");
      CompletionCompiler compiler = new CompletionCompiler(database, 
            FindForFunction.class,
            FindForVariable.class,
            FindInScopeMatching.class,
            FindConstructorsInScope.class,
            FindPossibleImports.class);
      
      CompletionRequest request = new CompletionRequest();
      
      System.err.println(SOURCE);
      
      request.setComplete("point.g");
      request.setSource(SOURCE);
      request.setLine(22);
      request.setResource("/example.snap");
      
      Map<String, String> completion = compiler.compile(request).getTokens();
      
      assertNotNull(completion.get("getX()"));
      assertNotNull(completion.get("getY()"));
      assertEquals(completion.get("getX()"), "function");
      assertEquals(completion.get("getY()"), "function");
      
      request = new CompletionRequest();
      
      request.setComplete("builder.ap");
      request.setSource(SOURCE);
      request.setLine(22);
      request.setResource("/other.snap");
      
      completion = compiler.compile(request).getTokens();
      
      assertNotNull(completion.get("append(a)"));
      assertNotNull(completion.get("append(a, b, c)"));
      assertEquals(completion.get("append(a)"), "function");
      assertEquals(completion.get("append(a, b, c)"), "function");
   }
}