package org.snapscript.studio.index.complete;

import java.util.Map;

import junit.framework.TestCase;

import org.snapscript.common.store.ClassPathStore;
import org.snapscript.common.thread.ThreadPool;
import org.snapscript.compile.StoreContext;
import org.snapscript.core.Context;
import org.snapscript.studio.index.IndexPathTranslator;
import org.snapscript.studio.index.Indexer;
import org.snapscript.studio.index.MockIndexDatabase;

public class CompletionCompilerTest extends TestCase {

   private static final String SOURCE_TO_REPLACE = "// replace me";
   private static final String SOURCE = 
   "class SomePath {\n"+
   "   var memb1: TypeEnum;\n"+
   "   var memb2: InnerClass;\n"+
   "   findSomething(index){\n"+
   "      // replace me\n"+
   "   }\n"+
   "   doSomething(index) {\n"+
   "      return 'foo'+index;\n"+
   "   }\n"+
   "   doSomething(){\n"+
   "      return 'foo';\n"+
   "   }\n"+
   "   class InnerClass {\n"+
   "      var x: String;\n"+
   "      var length: Integer;\n"+
   "      new(x, length){\n"+
   "         this.x = x;\n"+
   "         this.length = length;\n"+
   "      }\n"+
   "      someInnerFunc(): TypeEnum{\n"+
   "         return TypeEnum.ONE;\n"+
   "      }\n"+
   "   }\n"+
   "}\n"+
   "enum TypeEnum{\n"+
   "   ONE,\n"+
   "   TWO,\n"+
   "   THREE;\n"+
   "   at(index){\n"+
   "      return values[index];\n"+
   "   }\n"+
   "}\n";

   
   public void testCompletionCompiler() throws Exception {
      MockIndexDatabase database = new MockIndexDatabase();
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      IndexPathTranslator translator = new IndexPathTranslator();
      ThreadPool pool = new ThreadPool(1);
      Indexer indexer = new Indexer(translator, database, context, pool, null);
      database.setIndexer(indexer);
      CompletionCompiler compiler = new CompletionCompiler(database, 
            FindForFunction.class,
            FindForVariable.class,
            FindInScopeMatching.class,
            FindConstructorsInScope.class);
      
      CompletionRequest request = buildRequest(SOURCE, "do");
      Map<String, String> completion = compiler.compile(request).getTokens();
      
      assertNotNull(completion.get("doSomething()"));
      assertNotNull(completion.get("doSomething(index)"));
      assertEquals(completion.get("doSomething()"), "member-function");
      assertEquals(completion.get("doSomething(index)"), "member-function");
      
      request = buildRequest(SOURCE, "memb");
      completion = compiler.compile(request).getTokens();
      
      assertNotNull(completion.get("memb1"));
      assertNotNull(completion.get("memb2"));
      assertEquals(completion.get("memb1"), "property");
      assertEquals(completion.get("memb2"), "property");
      
      request = buildRequest(SOURCE, "memb1");
      completion = compiler.compile(request).getTokens();
      
      assertNotNull(completion.get("memb1"));
      assertNull(completion.get("memb2"));
      assertEquals(completion.get("memb1"), "property");
      
      request = buildRequest(SOURCE, "InnerClass.");
      completion = compiler.compile(request).getTokens();
      
      assertNotNull(completion.get("x"));
      assertNotNull(completion.get("length"));
      assertNotNull(completion.get("someInnerFunc()"));
      assertEquals(completion.get("x"), "property");
      assertEquals(completion.get("length"), "property");
      assertEquals(completion.get("someInnerFunc()"), "member-function");
      
      request = buildRequest(SOURCE, "InnerClass.l");
      completion = compiler.compile(request).getTokens();
      
      assertNull(completion.get("x"));
      assertNotNull(completion.get("length"));
      assertNull(completion.get("someInnerFunc()"));
      assertEquals(completion.get("length"), "property");
      
      request = buildRequest(SOURCE, "");
      completion = compiler.compile(request).getTokens();
      
      assertNotNull(completion.get("SomePath"));
      assertNotNull(completion.get("memb1"));
      assertNotNull(completion.get("memb2"));
      assertNotNull(completion.get("findSomething(index)"));
      assertNotNull(completion.get("doSomething(index)"));
      assertNotNull(completion.get("doSomething()"));
      assertNotNull(completion.get("InnerClass"));
      assertNotNull(completion.get("TypeEnum"));
      assertEquals(completion.get("SomePath"), "class");
      assertEquals(completion.get("memb1"), "property");
      assertEquals(completion.get("memb2"), "property");
      assertEquals(completion.get("findSomething(index)"), "member-function");
      assertEquals(completion.get("doSomething(index)"), "member-function");
      assertEquals(completion.get("doSomething()"), "member-function");
      assertEquals(completion.get("InnerClass"), "class");
      assertEquals(completion.get("TypeEnum"), "enum");
      
      request = buildRequest(SOURCE, "new ");
      completion = compiler.compile(request).getTokens();
      
      assertNotNull(completion.get("InnerClass(x, length)"));
      assertEquals(completion.get("InnerClass(x, length)"), "constructor");
   }
   
   
   private static CompletionRequest buildRequest(String source, String expression) {
      StringBuilder builder = new StringBuilder();
      CompletionRequest request = new CompletionRequest();
      String lines[] = source.split("\\r?\\n");
      int line = -1;
      
      for(int i = 0; i < lines.length; i++){
         String entry = lines[i];
      
         if(entry.contains(SOURCE_TO_REPLACE)) {
            builder.append("");
            line = i + 1;
         } else {
            builder.append(entry);
         }
         builder.append("\n");
      }
      if(line == -1) {
         throw new IllegalStateException("Could not find " + SOURCE_TO_REPLACE);
      }
      String formatted = builder.toString();
      
      request.setComplete(expression);
      request.setLine(line);
      request.setResource("/some/resource.snap");
      request.setSource(formatted);
      
      return request;
   }
}
