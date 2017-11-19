package org.snapscript.studio.index.classpath;

import java.util.Set;

import junit.framework.TestCase;

import org.snapscript.studio.index.IndexNode;

public class BootstrapClassPathTest extends TestCase {

   public void testScanner() throws Exception {
      Set<IndexNode> nodes = BootstrapClassPath.getBootstrapClasses();
      
      for(IndexNode node : nodes) {
         String fullName = node.getFullName();
         System.err.println(fullName);
      }
   }
}
