package org.snapscript.studio.index.classpath;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;

import org.snapscript.studio.index.IndexNode;
import org.snapscript.studio.index.IndexType;

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
   
   private static String getAbsolutePath(ClassIndexNode node) {
      URL url = node.getURL();
      
      if(url.toString().toLowerCase().startsWith("jar:file")) {
         try {
            JarURLConnection connection = (JarURLConnection) url.openConnection();
            JarFile jarFile = connection.getJarFile();
            File file = new File(jarFile.getName());
            
            return file.getCanonicalPath();
         } catch(Exception e) {
            //workspace.getLogger().trace("Could not build a JAR path", e);
         }
      }
      return getFullPath(node);
   }
   
   private static String getResource(ClassIndexNode node) {
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
   
   private static String getFullName(String path) {
      if(path.startsWith("/") || path.startsWith("\\")) {
         path = path.substring(1);
      }
      int length = path.length();
      int extension = ".class".length();
      
      path = path.substring(0, length - extension);
      path = path.replace('/', '.');
      path = path.replace('\\', '.');
      
      return path;
   }
   
   private static String getTypeName(String path) {
      String fullName = getFullName(path);
      int index = fullName.lastIndexOf('.');
      
      if(index != -1) {
         return fullName.substring(index + 1);
      }
      return fullName;
   }
   
   private static String getName(String path) {
      String typeName = getTypeName(path);
      int index = typeName.lastIndexOf('$');
      
      if(index != -1) {
         return typeName.substring(index + 1);
      }
      return typeName;
   }

   private static class ClassIndexNode implements IndexNode {
      
      private ClassInfo info;
      private Class type;
      private String absolute;
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
            resource = ClassPathIndexScanner.getResource(this);
         }
         return resource;
      }
      
      @Override
      public String getAbsolutePath(){
         if(absolute == null) {
            absolute = ClassPathIndexScanner.getAbsolutePath(this);
         }
         return absolute;
      } 
       
      @Override
      public String getName() {
         return ClassPathIndexScanner.getName(info.getResourceName());
      }

      @Override
      public String getTypeName() {
         return ClassPathIndexScanner.getTypeName(info.getResourceName());
      }

      @Override
      public String getFullName() {
         return ClassPathIndexScanner.getFullName(info.getResourceName());
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
         Class type = getNodeClass();
         
         if(type != null) {
            if(type.isInterface()) {
               return IndexType.TRAIT;
            }
            if(type.isEnum()) {
               return IndexType.ENUM;
            }
         }
         return IndexType.CLASS;
      }

      @Override
      public Set<IndexNode> getNodes() {
         return Collections.emptySet();
      }
      
      public URL getURL() {
         if(url == null) {
            url = info.url();
         }
         return url;
      }
      
      private Class getNodeClass() {
         if(type == null) {
            try {
               type = info.load();
            } catch(Throwable e) {
               return null;
            }
         }
         return type;
      }
      
   }
}
