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

public class StaticMethodCompletionTest extends TestCase {
   
   public void testStaticClassCompletion() throws Exception {
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
      
      CompletionRequest request = new CompletionRequest();
      
      request.setComplete("System.cu");
      request.setSource("\nclass SomeClass{}\n");
      request.setLine(1);
      request.setResource("/example.snap");
      
      Map<String, String> completion = compiler.compile(request).getTokens();
      
      assertNotNull(completion.get("currentTimeMillis()"));
      assertEquals(completion.get("currentTimeMillis()"), "function");
      
      request = new CompletionRequest();
      
      request.setComplete("Runtime.g");
      request.setSource("\nclass SomeClass{}\n");
      request.setLine(1);
      request.setResource("/other.snap");
      
      completion = compiler.compile(request).getTokens();
      
      assertNotNull(completion.get("getRuntime()"));
      assertEquals(completion.get("getRuntime()"), "function");
      
      request = new CompletionRequest();
      
      request.setComplete("Integer.");
      request.setSource("\nclass SomeClass{}\n");
      request.setLine(1);
      request.setResource("/blah.snap");
      
      completion = compiler.compile(request).getTokens();
      
      assertNotNull(completion.get("MAX_VALUE"));
      assertNotNull(completion.get("MIN_VALUE"));
      assertNotNull(completion.get("valueOf(a)"));
      assertEquals(completion.get("MAX_VALUE"), "property");
      assertEquals(completion.get("MIN_VALUE"), "property");
      assertEquals(completion.get("valueOf(a)"), "function");
   }
}