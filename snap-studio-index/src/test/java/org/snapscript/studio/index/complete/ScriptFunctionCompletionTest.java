package org.snapscript.studio.index.complete;

import java.io.File;
import java.util.Map;

import org.snapscript.common.store.ClassPathStore;
import org.snapscript.common.thread.ThreadPool;
import org.snapscript.compile.StoreContext;
import org.snapscript.core.Context;
import org.snapscript.studio.index.IndexDatabase;
import org.snapscript.studio.index.IndexScanner;

import junit.framework.TestCase;

public class ScriptFunctionCompletionTest extends TestCase {
   
   private static final String SOURCE_1 =
   "import util.regex.Pattern;\n"+
   "function func(p: Pattern, s: String) {\n"+     
   "   // replace me\n"+
   "}\n";

   private static final String SOURCE_2 =
   "import util.regex.Pattern;\n"+
   "function func(p: Pattern, s: String)\n" +
   "{\n"+     
   "   // replace me\n"+
   "}\n";
   
   private static final String SOURCE_3 =
   "import util.regex.Pattern;\n"+
   "function func(p: Pattern, s: String)\n" +
   "// some comment\n" +
   "{\n"+     
   "   // replace me\n"+
   "}\n";
   
   private static final String SOURCE_4 =
   "import util.regex.Pattern;\n"+
   "function func(p: Pattern, s: String) { // comment\n"+
   "   // replace me\n"+
   "}\n";
   
   private static final String SOURCE_5 =
   "import util.regex.Pattern;\n"+
   "class Foo {\n"+
   "   func(p: Pattern, s: String) {\n"+
   "      // replace me\n"+
   "   }\n"+
   "}";
   
   public void testScriptFunction() throws Exception {
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      ThreadPool pool = new ThreadPool(2);
      File file = File.createTempFile("test", getClass().getSimpleName());
      IndexDatabase database = new IndexScanner(ClassLoader.getSystemClassLoader(), context, pool, file, "test");
      CompletionCompiler compiler = new CompletionCompiler(database, 
            FindForExpression.class,
            FindInScopeMatching.class,
            FindConstructorsInScope.class,
            FindPossibleImports.class);
      
      checkSource(compiler, SOURCE_1);
      checkSource(compiler, SOURCE_2);
      checkSource(compiler, SOURCE_3);
      checkSource(compiler, SOURCE_4);
      checkSource(compiler, SOURCE_5);
   }

   private void checkSource(CompletionCompiler compiler, String source) throws Exception {
      CompletionRequest request = SourceCodeInterpolator.buildRequest(source, "p.c");
      CompletionResponse response = compiler.compile(request);
      Map<String, String> completion = response.getTokens();
      
      System.err.println(response.getDetails());
      
      assertNotNull(completion.get("compile(a)"));
      assertNotNull(completion.get("compile(a, b)"));
      assertEquals(completion.get("compile(a)"), "function");
      assertEquals(completion.get("compile(a, b)"), "function");
   }
}
