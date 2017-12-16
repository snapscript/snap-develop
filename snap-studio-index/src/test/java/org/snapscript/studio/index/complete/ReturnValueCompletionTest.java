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

public class ReturnValueCompletionTest extends TestCase {
   
   private static final String SOURCE = 
   "import util.stream.Collectors;\n"+
   "function func(list: List) {\n"+
   "   return // replace me\n"+
   "}\n";
         
   public void testReturnCompletion() throws Exception {
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
      
      CompletionRequest request = SourceCodeInterpolator.buildRequest(SOURCE, "return list.stream().filter(x -> x.bool).coll");
      CompletionResponse response = compiler.completeExpression(request);
      Map<String, String> completion = response.getTokens();
     
      assertNotNull(completion);
   }
}
