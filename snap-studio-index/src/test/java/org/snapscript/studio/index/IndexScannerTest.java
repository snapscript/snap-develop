package org.snapscript.studio.index;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import junit.framework.TestCase;

import org.snapscript.common.store.ClassPathStore;
import org.snapscript.compile.StoreContext;
import org.snapscript.core.Context;

public class IndexScannerTest extends TestCase {
   
   public void testScanner() throws Exception {
      UncaughtExceptionHandler handler = new UncaughtExceptionHandler() {
         
         @Override
         public void uncaughtException(Thread thread, Throwable cause) {
            cause.printStackTrace();
         }
      };
      Thread.setDefaultUncaughtExceptionHandler(handler);
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      File root = new File("c:/Work/development/snapscript/snap-develop/snap-studio/work/demo");
      IndexScanner scanner = new IndexScanner(context, root, "demo");
      long start = System.currentTimeMillis();
      Map<String, IndexNode> nodes = scanner.findTypesMatching(".*");
      long finish = System.currentTimeMillis();
      System.err.println("time="+(finish-start));
      Set<Entry<String, IndexNode>> entries = nodes.entrySet();
      
      for(Entry<String, IndexNode> entry : entries) {
         String name = entry.getKey();
         IndexNode node = entry.getValue();
         String fullName = node.getFullName();
         
         System.err.println(fullName);
      }
   }

}
