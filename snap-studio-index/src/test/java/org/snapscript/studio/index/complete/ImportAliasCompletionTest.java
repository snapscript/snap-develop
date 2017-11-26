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

public class ImportAliasCompletionTest extends TestCase {

   public void testImportCompletionForEmptySource() throws Exception {
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
      
      CompletionRequest request = new CompletionRequest();
      
      request.setComplete("Au");
      request.setSource("import sound.sampled.AudioFormat;\n\n"); // this is an alias for javax.sound.sampled.AudioFormat
      request.setLine(2);
      request.setResource("/example.snap");
      
      Map<String, String> completion = compiler.compile(request).getTokens();
      
      assertNotNull(completion.get("AudioFormat"));
      assertNotNull(completion.get("AutoCloseable"));
      assertEquals(completion.get("AudioFormat"), "class");
      assertEquals(completion.get("AutoCloseable"), "trait");
   }
}
