package org.snapscript.studio.index;

import java.io.File;
import java.util.Map;

import junit.framework.TestCase;

import org.snapscript.common.store.ClassPathStore;
import org.snapscript.common.thread.ThreadPool;
import org.snapscript.compile.StoreContext;
import org.snapscript.core.Context;

public class IndexSearcherTest extends TestCase {

   public void testIndexSearcher() throws Exception {
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      ThreadPool pool = new ThreadPool(2);
      File file = File.createTempFile("test", getClass().getSimpleName());
      IndexDatabase database = new IndexScanner(ClassLoader.getSystemClassLoader(), context, pool, file, "test");
      IndexFile indexFile = database.getFile("/file.snap", "class X extends HashMap with Runnable {\nconst x = 0;\n}\n");
      IndexNode root = indexFile.getRootNode();
      String detail = IndexDumper.dump(root);
      
      System.err.println(detail);
      
      IndexNode node = indexFile.getNodeAtLine(2);
      Map<String, IndexNode> nodes = database.getNodesInScope(node);
      
      assertNotNull(nodes.get("x"));
      assertNotNull(nodes.get("run()"));
      assertNotNull(nodes.get("get(a)"));
      assertNotNull(nodes.get("put(a, b)"));
      assertNotNull(nodes.get("containsKey(a)"));
      assertEquals(nodes.get("x").getType(), IndexType.PROPERTY);
      assertEquals(nodes.get("run()").getType(), IndexType.MEMBER_FUNCTION);
      assertEquals(nodes.get("get(a)").getType(), IndexType.MEMBER_FUNCTION);
      assertEquals(nodes.get("put(a, b)").getType(), IndexType.MEMBER_FUNCTION);
      assertEquals(nodes.get("containsKey(a)").getType(), IndexType.MEMBER_FUNCTION);
   }
}
