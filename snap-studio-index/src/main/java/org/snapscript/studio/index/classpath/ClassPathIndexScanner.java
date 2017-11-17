package org.snapscript.studio.index.classpath;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.snapscript.studio.index.IndexNode;

import com.google.common.reflect.ClassPath.ClassInfo;

public class ClassPathIndexScanner {
   
   private final Set<ClassInfo> types;
   
   public ClassPathIndexScanner(Set<ClassInfo> classPath) {
      this.types = new HashSet<ClassInfo>();
      
      try {
         types.addAll(classPath);
         types.addAll(ClassPathBootstrapScanner.getBootstrapClasses());
      }catch(Throwable e) {
         e.printStackTrace();
      }
   }
   
   public Map<String, IndexNode> getTypeNodesMatching(String expression) {
      Map<String, IndexNode> nodes = new HashMap<String, IndexNode>();
      
      for(ClassInfo info : types) {
         String resourcePath = info.getResourceName();
         
         if(!resourcePath.startsWith("target/")) {
            IndexNode node = new ClassIndexNode(info);
            String name = node.getFullName();

            if(!name.isEmpty() && name.matches(expression)) {
               if(name.startsWith("java.")) {
                  String shortName = name.substring(5);
                  nodes.put(shortName, node);
               }
//               else if(name.startsWith("javax.")) {
//                  String shortName = name.substring(6);
//                  nodes.put(shortName, node);
//               }
               nodes.put(name, node);
            }
         }
      }
      return nodes;
   }
   
   public Map<String, IndexNode> getTypeNodes() throws Exception {
      Map<String, IndexNode> nodes = new HashMap<String, IndexNode>();
      
      for(ClassInfo info : types) {
         String resourcePath = info.getResourceName();
         
         if(!resourcePath.startsWith("target/")) {
            IndexNode node = new ClassIndexNode(info);
            String name = node.getFullName();
            
            if(!name.isEmpty()) {
               if(name.startsWith("java.")) {
                  String shortName = name.substring(5);
                  nodes.put(shortName, node);
               }
//               else if(name.startsWith("javax.")) {
//                  String shortName = name.substring(6);
//                  nodes.put(shortName, node);
//               }
               nodes.put(name, node);
            }
         }
      }
      return nodes;
   }
}
