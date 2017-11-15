package org.snapscript.studio.index.classpath;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.snapscript.studio.index.IndexNode;
import org.snapscript.studio.index.IndexType;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

public class ClassPathIndexScanner {

   private final ClassLoader loader;
   
   public ClassPathIndexScanner(ClassLoader loader) {
      this.loader = loader;
   }
   
   public Map<String, IndexNode> getTypeNodes() throws Exception {
      ImmutableSet<ClassInfo> types = ClassPath.from(loader).getAllClasses();
      
      if(!types.isEmpty()) {
         return getTypeNodes(types);
      }
      return Collections.emptyMap();
   }
   
   public static Map<String, IndexNode> getTypeNodesMatching(Set<ClassInfo> types, String expression) {
      Map<String, IndexNode> nodes = new HashMap<String, IndexNode>();
      
      for(ClassInfo info : types) {
         String resourcePath = info.getResourceName();
         
         if(!resourcePath.startsWith("target/")) {
            IndexNode node = new ClassIndexNode(info);
            String name = node.getName();
            
            if(!name.isEmpty() && name.matches(expression)) {
               nodes.put(name, node);
            }
         }
      }
      return nodes;
   }
   
   public static Map<String, IndexNode> getTypeNodes(Set<ClassInfo> types) throws Exception {
      Map<String, IndexNode> nodes = new HashMap<String, IndexNode>();
      
      for(ClassInfo info : types) {
         String resourcePath = info.getResourceName();
         
         if(!resourcePath.startsWith("target/")) {
            IndexNode node = new ClassIndexNode(info);
            String name = node.getName();
            
            if(!name.isEmpty()) {
               nodes.put(name, node);
            }
         }
      }
      return nodes;
   }
   
   private static String getClassLocation(ClassIndexNode node) {
      URL url = node.getURL();
      
      if(url.toString().toLowerCase().startsWith("jar:file")) {
         try {
            JarURLConnection connection = (JarURLConnection) url.openConnection();
            URL jarUrl = connection.getJarFileURL();
            File file = new File(jarUrl.toURI());
            
            return file.getCanonicalFile().getName();
         } catch(Exception e) {
            //workspace.getLogger().trace("Could not build a JAR path", e);
         }
      }
      return getFullPath(node);
   }
   
   private static String getFullPath(ClassIndexNode node) {
      String path = node.getName();
      
      if(!path.startsWith("/")) {
         path = "/" + path;
      }
      if(path.endsWith(".class")) {
         path = path.substring(0, path.lastIndexOf(".class"));
      }
      if(path.contains("$")) {
         return path.substring(0, path.indexOf('$'));
      }
      return path.replace(".", "/") + ".java";
   }

   private static class ClassIndexNode implements IndexNode {
      
      private ClassInfo info;
      private Class type;
      private String resource;
      private URL url;
      
      public ClassIndexNode(ClassInfo info) {
         this.info = info;
      }
      
      @Override
      public int getLine() {
         return -1;
      }
      
      @Override
      public String getResource(){
         if(resource == null) {
            resource = getClassLocation(this);
         }
         return resource;
      }
       

      @Override
      public String getName() {
         return getNodeClass().getSimpleName();
      }

      @Override
      public String getTypeName() {
         return getNodeClass().getSimpleName();
      }

      @Override
      public String getFullName() {
         return getNodeClass().getName();
      }

      @Override
      public IndexNode getConstraint() {
         return null;
      }

      @Override
      public IndexNode getParent() {
         return null;
      }

      @Override
      public IndexType getType() {
         if(getNodeClass().isInterface()) {
            return IndexType.TRAIT;
         }
         if(getNodeClass().isEnum()) {
            return IndexType.ENUM;
         }
         return IndexType.CLASS;
      }

      @Override
      public Set<IndexNode> getNodes() {
         return null;
      }
      
      public URL getURL() {
         if(url == null) {
            url = info.url();
         }
         return url;
      }
      
      private Class getNodeClass() {
         if(type == null) {
            type = info.load();
         }
         return type;
      }
      
   }
}
