package org.snapscript.studio.index;

import java.io.File;
import java.util.Map;

import junit.framework.TestCase;

import org.snapscript.common.store.ClassPathStore;
import org.snapscript.common.thread.ThreadPool;
import org.snapscript.compile.StoreContext;
import org.snapscript.core.Context;

public class DefaultConstructorTest extends TestCase {

   private static final String SOURCE_1 =
   "class DoubleComparator with Comparator{\n"+
   "   compare(left, right) {\n"+
   "     return Double.compare(left, right);\n"+
   "   }\n"+      
   "}";
   
   private static final String SOURCE_2 =
   "class DoubleComparator with Comparator{\n"+
   "   const reverse;\n"+
   "   new(reverse) {\n"+
   "      this.reverse = reverse;\n"+
   "   }\n"+
   "   compare(left, right) {\n"+
   "     return Double.compare(left, right);\n"+
   "   }\n"+      
   "}";
   
   public void testDefaultConstructors() throws Exception {
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      ThreadPool pool = new ThreadPool(2);
      File file = File.createTempFile("test", getClass().getSimpleName());
      IndexDatabase database = new IndexScanner(ClassLoader.getSystemClassLoader(), context, pool, file, "test");
      IndexPathTranslator translator = new IndexPathTranslator();
      Indexer indexer = new Indexer(translator, database, context, pool, null);
      IndexFile searcher = indexer.index("/double.snap", SOURCE_1);
      Map<String, IndexNode> nodes = searcher.getNodesInScope(1);
   
      assertNotNull(nodes.get("DoubleComparator()"));
      assertNotNull(nodes.get("compare(left, right)"));
      assertEquals(nodes.get("DoubleComparator()").getType(), IndexType.CONSTRUCTOR);
      assertEquals(nodes.get("compare(left, right)").getType(), IndexType.MEMBER_FUNCTION);
   }
   
   public void testNoDefaultConstructors() throws Exception {
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      ThreadPool pool = new ThreadPool(2);
      File file = File.createTempFile("test", getClass().getSimpleName());
      IndexDatabase database = new IndexScanner(ClassLoader.getSystemClassLoader(), context, pool, file, "test");
      IndexPathTranslator translator = new IndexPathTranslator();
      Indexer indexer = new Indexer(translator, database, context, pool, null);
      IndexFile searcher = indexer.index("/double.snap", SOURCE_2);
      Map<String, IndexNode> nodes = searcher.getNodesInScope(1);

      assertNull(nodes.get("DoubleComparator()"));
      assertNotNull(nodes.get("DoubleComparator(reverse)"));
      assertNotNull(nodes.get("compare(left, right)"));
      assertEquals(nodes.get("DoubleComparator(reverse)").getType(), IndexType.CONSTRUCTOR);
      assertEquals(nodes.get("compare(left, right)").getType(), IndexType.MEMBER_FUNCTION);
   }
}
