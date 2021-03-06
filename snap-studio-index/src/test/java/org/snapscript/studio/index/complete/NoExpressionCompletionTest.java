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
import org.snapscript.studio.index.config.SystemIndexConfigFile;

public class NoExpressionCompletionTest extends TestCase {

   private static final String SOURCE =
   "class TextBuffer {\n"+
   "   var builder: StringBuilder;\n"+
   "   append(source: String, offset: Integer, length: Integer) {\n"+
   "      // replace me\n"+
   "   }\n"+
   "}\n";
         
   public void testCompletionInForScope() throws Exception {
      System.err.println(SOURCE);
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      ThreadPool pool = new ThreadPool(2);
      File file = File.createTempFile("test", getClass().getSimpleName());
      IndexDatabase database = new IndexScanner(SystemIndexConfigFile.getSystemClassPath(), context, pool, file, "test");
      CompletionCompiler compiler = new CompletionCompiler(database, 
            FindForExpression.class,
            FindInScopeMatching.class,
            FindConstructorsInScope.class,
            FindPossibleImports.class);
      
      CompletionRequest request = SourceCodeInterpolator.buildRequest(SOURCE, "");
      CompletionResponse response = compiler.completeExpression(request);
      Map<String, String> completion = response.getTokens();
      
      System.err.println(response.getDetails());
      
      assertNotNull(completion.get("Integer"));
      assertNotNull(completion.get("ArrayList"));
      assertNotNull(completion.get("append(source, offset, length)"));
      assertNotNull(completion.get("builder"));
      assertEquals(completion.get("Integer"), "class");
      assertEquals(completion.get("ArrayList"), "class");
      assertEquals(completion.get("append(source, offset, length)"), "function");
      assertEquals(completion.get("builder"), "property");
      assertEquals(completion.get("StringBuilder"), "class");
   }
}