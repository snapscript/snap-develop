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
         
         if(!isTargetResource(resourcePath)) {
            IndexNode node = new ClassIndexNode(info);
            String fullName = node.getFullName();
            String name = node.getName();

            if(!name.isEmpty() && name.matches(expression)) {
               String[] names = getNames(fullName);
               
               for(String entry : names) {
                  nodes.put(entry, node);
               }
            }
         }
      }
      return nodes;
   }
   
   public Map<String, IndexNode> getTypeNodes() throws Exception {
      Map<String, IndexNode> nodes = new HashMap<String, IndexNode>();
      
      for(ClassInfo info : types) {
         String resourcePath = info.getResourceName();
         
         if(!isTargetResource(resourcePath)) {
            IndexNode node = new ClassIndexNode(info);
            String name = node.getFullName();
            
            if(!name.isEmpty()) {
               String[] names = getNames(name);
               
               for(String entry : names) {
                  nodes.put(entry, node);
               }
            }
         }
      }
      return nodes;
   }
   
   private static String[] getNames(String fullName) {
      if(fullName.startsWith("java.")) {
         String shortName = fullName.substring(5);
         return new String[]{ shortName, fullName };
      }
      if(fullName.startsWith("javax.")) {
         String shortName = fullName.substring(6);
         return new String[]{ shortName, fullName };
      }
      return new String[]{ fullName};
   }
   
   public static boolean isTargetResource(String resourcePath) {
      return resourcePath.startsWith("target/");
   }
}
