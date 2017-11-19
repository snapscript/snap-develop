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

public class ClassCompletionTest extends TestCase {
   
   public void testClassCompletion() throws Exception {
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
      
      request.setComplete("S");
      request.setSource("\nclass SomeClass{}\n");
      request.setLine(1);
      request.setResource("/example.snap");
      
      Map<String, String> completion = compiler.compile(request).getTokens();
      
      assertNotNull(completion.get("String"));
      assertNotNull(completion.get("System"));
      assertNotNull(completion.get("SomeClass"));
      assertEquals(completion.get("String"), "class");
      assertEquals(completion.get("System"), "class");
      assertEquals(completion.get("SomeClass"), "class");
      
      request = new CompletionRequest();
      
      request.setComplete("Hash");
      request.setSource("\nclass SomeClass{}\n");
      request.setLine(1);
      request.setResource("/other.snap");
      
      completion = compiler.compile(request).getTokens();
      
      assertNotNull(completion.get("HashMap"));
      assertNotNull(completion.get("HashSet"));
      assertEquals(completion.get("HashMap"), "class");
      assertEquals(completion.get("HashSet"), "class");
   }
}