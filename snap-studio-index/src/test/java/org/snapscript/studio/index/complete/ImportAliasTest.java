package org.snapscript.studio.index.complete;

import java.io.File;
import java.util.Map;

import junit.framework.TestCase;

import org.snapscript.common.store.ClassPathStore;
import org.snapscript.common.thread.ThreadPool;
import org.snapscript.compile.StoreContext;
import org.snapscript.core.Context;
import org.snapscript.studio.index.IndexDatabase;
import org.snapscript.studio.index.SourceFile;
import org.snapscript.studio.index.IndexNode;
import org.snapscript.studio.index.IndexScanner;
import org.snapscript.studio.index.IndexType;

public class ImportAliasTest extends TestCase {
   
   public void testImportAlias() throws Exception {
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      ThreadPool pool = new ThreadPool(2);
      File file = File.createTempFile("test", getClass().getSimpleName());
      IndexDatabase database = new IndexScanner(ClassLoader.getSystemClassLoader(), context, pool, file, "test");
      SourceFile resource = database.getFile("/test.snap", "import sound.sampled.AudioFormat;");
      IndexNode root = resource.getRootNode();
      Map<String, IndexNode> nodes = database.getNodesInScope(root);
      
      assertNotNull(nodes.get("AudioFormat"));
      assertEquals(nodes.get("AudioFormat").getType(), IndexType.IMPORT);
      
      String fullName = nodes.get("AudioFormat").getFullName();
      IndexNode node = database.getTypeNode(fullName);
      
      assertNotNull(node);
      assertEquals(node.getType(), IndexType.CLASS);
      assertEquals(node.getFullName(), "javax.sound.sampled.AudioFormat");
   }

}
