package org.snapscript.studio.index.classpath;

import java.util.Set;

import junit.framework.TestCase;

import com.google.common.reflect.ClassPath.ClassInfo;

public class ClassPathBootstrapScannerTest extends TestCase {

   public void testScanner() throws Exception {
      Set<ClassInfo> types = ClassPathBootstrapScanner.getBootstrapClasses();
      for(ClassInfo type : types) {
         System.err.println(type);
      }
   }
}
