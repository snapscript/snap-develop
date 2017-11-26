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

public class ExtendOrImplementCompletionTest extends TestCase {

   public void testExtends() throws Exception {
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      ThreadPool pool = new ThreadPool(2);
      File file = File.createTempFile("test", getClass().getSimpleName());
      IndexDatabase database = new IndexScanner(ClassLoader.getSystemClassLoader(), context, pool, file, "test");
      CompletionCompiler compiler = new CompletionCompiler(database, 
            FindForExpression.class,
            FindTypesToExtend.class,
            FindTraitToImplement.class,
            FindInScopeMatching.class,
            FindConstructorsInScope.class,
            FindPossibleImports.class);
      
      CompletionRequest request = SourceCodeInterpolator.buildRequest("// replace me", "class Foo extends H");
      Map<String, String> completion = compiler.compile(request).getTokens();
   
      assertNotNull(completion.get("HashMap"));
      assertNotNull(completion.get("HashSet"));
      assertEquals(completion.get("HashMap"), "class");
      assertEquals(completion.get("HashSet"), "class");  
      
      request = SourceCodeInterpolator.buildRequest("// replace me", "class Foo extends R");
      completion = compiler.compile(request).getTokens();
   
      assertNotNull(completion.get("Random"));
      assertNull(completion.get("Runnable"));
      assertEquals(completion.get("Random"), "class");
      
      for(String value : completion.values()) {
         assertEquals(value, "class");
      }
   }
   
   
   public void testImplements() throws Exception {
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      ThreadPool pool = new ThreadPool(2);
      File file = File.createTempFile("test", getClass().getSimpleName());
      IndexDatabase database = new IndexScanner(ClassLoader.getSystemClassLoader(), context, pool, file, "test");
      CompletionCompiler compiler = new CompletionCompiler(database, 
            FindForExpression.class,
            FindTypesToExtend.class,
            FindTraitToImplement.class,
            FindInScopeMatching.class,
            FindConstructorsInScope.class,
            FindPossibleImports.class);
      
      CompletionRequest request = SourceCodeInterpolator.buildRequest("// replace me", "class Foo with R");
      Map<String, String> completion = compiler.compile(request).getTokens();
   
      assertNotNull(completion.get("Runnable"));
      assertNull(completion.get("Random"));
      assertEquals(completion.get("Runnable"), "trait");
      
      for(String value : completion.values()) {
         assertEquals(value, "trait");
      }
   }
}
