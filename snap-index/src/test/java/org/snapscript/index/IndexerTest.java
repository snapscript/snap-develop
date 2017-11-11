package org.snapscript.index;

import java.util.Set;

import org.snapscript.common.store.ClassPathStore;
import org.snapscript.compile.StoreContext;
import org.snapscript.core.Context;

import junit.framework.TestCase;

public class IndexerTest extends TestCase {

   private static final String SOURCE =
   "import lang.String;\n"+
   "import util.concurrent.ConcurrentHashMap;\n"+
   "class SomeClass {\n"+
   "   var memb = 11.0;\n"+
   "   test(){\n"+
   "     var str = 'xxx';\n"+
   "     println(str);\n"+
   "   }\n"+      
   "   class InnerClass{}\n"+
   "}\n"+
   "enum SizeEnum{\n"+
   "   BIG,\n"+
   "   SMALL,\n"+
   "   TINY;\n"+
   "   func(){\n"+
   "      var a = 1;\n"+
   "      var b = 2;\n"+
   "      var c = 3;\n"+
   "      return a+b+c;\n"+
   "   }\n"+
   "}\n"+
   "module Mod {\n"+
   "   class ModClass{\n"+
   "      const PROP = 'abc';\n"+
   "   }\n"+
   "   trait ModTrait{\n"+
   "      someTraitFunc(a,b,c);\n"+
   "   }\n"+
   "   modFunc(){}\n"+
   "}\n"+
   "const PI = 3.14;\n"+
   "var x =10;\n"+
   "function foo(a, b) {\n"+
   "   if(x > 0){\n"+
   "      var y = x;\n"+
   "      y++;\n"+
   "   }\n"+
   "   if(x  >0){\n"+
   "      var y = 0;\n"+
   "      y++;\n"+
   "   }\n"+
   "   if(x !=77){\n"+
   "      if(x > 0){\n"+
   "         var y =1;\n"+
   "         y--;\n"+
   "      } else {\n"+
   "         var y=33;\n"+
   "         y++;\n"+
   "      }\n"+
   "   }\n"+
   "}\n";
   
   public void testIndexer() throws Exception {
      Indexer indexer = new Indexer();
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      IndexSearcher searcher = indexer.index(context, "/some/path.snap", SOURCE);
      IndexNode node = searcher.getNode();
      
      traverse(node, "");
      
      assertEquals(searcher.getDepth(3), 1);
      assertEquals(searcher.getDepth(6), 2);
      assertEquals(searcher.getDepth(12), 1);
      assertEquals(searcher.getDepth(31), 0);
      assertEquals(searcher.getDepth(24), 2);
      assertEquals(searcher.getDepth(44), 3);
      
      assertEquals(searcher.getNode(3).getType(), IndexType.CLASS);
      assertEquals(searcher.getNode(3).getIndex().getName(), "SomeClass");
      
      assertEquals(searcher.getNode(6).getType(), IndexType.MEMBER_FUNCTION);
      assertEquals(searcher.getNode(6).getIndex().getName(), "test");
      
      assertEquals(searcher.getNode(12).getType(), IndexType.ENUM);
      assertEquals(searcher.getNode(12).getIndex().getName(), "SizeEnum");
      
      assertEquals(searcher.getNode(31).getType(), IndexType.SCRIPT);
      assertEquals(searcher.getNode(31).getIndex().getName(), "/some/path.snap");
      
      assertEquals(searcher.getNode(24).getType(), IndexType.CLASS);
      assertEquals(searcher.getNode(24).getIndex().getName(), "ModClass");
      
      assertEquals(searcher.getNode(44).getType(), IndexType.COMPOUND);
      assertEquals(searcher.getNode(44).getIndex().getName(), "");
   }
   
   private static void traverse(IndexNode node, String indent) throws Exception {
      if(node != null) {
         Set<IndexNode> nodes = node.getNodes();
         IndexType type = node.getType();
         String name = node.getIndex().getName();
         
         if(!type.isRoot()) {
            System.err.print(indent);
            
            if(!type.isCompound()) {
               System.err.print(type.getName() + " " + name + " ");
            }
            if(type.isLeaf()) {
               System.err.println();
            } else {
               System.err.println("{");
            }
         }
         for(IndexNode entry : nodes) {
            if(type.isRoot()) {
               traverse(entry, "");
            } else {
               traverse(entry, indent + "   ");
            }
         }
         if(!type.isRoot() && !type.isLeaf()) {
            System.err.println(indent + "}");
         }
      }
   }
}
