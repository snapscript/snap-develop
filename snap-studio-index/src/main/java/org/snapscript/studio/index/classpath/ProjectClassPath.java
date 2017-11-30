package org.snapscript.studio.index.classpath;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.snapscript.studio.index.IndexNode;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

public class ProjectClassPath {

   public static Set<IndexNode> getProjectClassPath(ClassLoader loader) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      
      try {
         Set<ClassInfo> projectClasses = ClassPath.from(loader).getAllClasses();
      
         for(ClassInfo info : projectClasses) {
            String path = info.getResourceName();
            
            if(!isTargetResource(path)) {
               IndexNode node = ClassIndexProcessor.getIndexNode(info);
               nodes.add(node);
            }
         }
      } catch(Throwable e) {
         //e.printStackTrace();
      }
      nodes.addAll(BootstrapClassPath.getBootstrapClasses());
      return Collections.unmodifiableSet(nodes);
   }
   
   private static boolean isTargetResource(String resourcePath) {
      return resourcePath.startsWith("target/");
   }
}
