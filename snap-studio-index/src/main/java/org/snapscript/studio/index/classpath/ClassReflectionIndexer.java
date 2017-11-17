package org.snapscript.studio.index.classpath;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;

import org.snapscript.studio.index.IndexNode;

import com.google.common.reflect.ClassPath.ClassInfo;

public class ClassReflectionIndexer {
   
   private static final Map<String, IndexNode> DEFAULT_CLASSES;
   private static final String[] DEFAULT_IMPORTS = {
      "java.lang.",
      "java.util.",
      "java.net.",
      "java.io."
   };
   
   static  {
      DEFAULT_CLASSES = new ConcurrentHashMap<String, IndexNode>();
   }
   
   
   public static IndexNode getDefaultImport(String name) {
      if(DEFAULT_CLASSES.isEmpty()) {
         Set<ClassInfo> list = ClassPathBootstrapScanner.getBootstrapClasses();
         
         for(ClassInfo info : list) {
            String fullName = getFullName(info);
            
            for(String prefix : DEFAULT_IMPORTS) {
               String shortName = getName(info);
               
               if(fullName.startsWith(prefix)) {
                  if(fullName.equals(prefix + shortName)) {
                     IndexNode node = getIndexNode(info);
                     DEFAULT_CLASSES.put(shortName, node);
                  }
               }
            }
         }
      }
      return DEFAULT_CLASSES.get(name);
   }
   
   public static Set<IndexNode> getChildren(ClassInfo info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      
      nodes.addAll(getMethods(info));
      nodes.addAll(getFields(info));
      nodes.addAll(getInnerClasses(info));
      
      return nodes;
   }

   public static Set<IndexNode> getMethods(ClassInfo info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      
      try{
         Class type = info.load();
         Method[] methods = type.getDeclaredMethods();
         
         for(Method method : methods) {
            IndexNode node = getIndexNode(method);
            nodes.add(node);
         }
      }catch(Throwable cause) {
         cause.printStackTrace();
      }
      return nodes;
   }
   
   public static Set<IndexNode> getFields(ClassInfo info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      
      try{
         Class type = info.load();
         Field[] fields = type.getDeclaredFields();
         
         for(Field field : fields) {
            IndexNode node = getIndexNode(field);
            nodes.add(node);
         }
      }catch(Throwable cause) {
         cause.printStackTrace();
      }
      return nodes;
   }
   
   public static Set<IndexNode> getInnerClasses(ClassInfo info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      
      try{
         Class type = info.load();
         Class[] children = type.getDeclaredClasses();
         
         for(Class child : children) {
            IndexNode node = getIndexNode(child);
            nodes.add(node);
         }
      }catch(Throwable cause) {
         cause.printStackTrace();
      }
      return nodes;
   }
   
   public static String getAbsolutePath(ClassInfo info) {
      URL url = info.url();
      String location = String.valueOf(url).toLowerCase();
      
      if(location.startsWith("jar:file")) {
         try {
            JarURLConnection connection = (JarURLConnection) url.openConnection();
            JarFile jarFile = connection.getJarFile();
            File file = new File(jarFile.getName());
            
            return file.getCanonicalPath();
         } catch(Throwable e) {}
      }
      return getFullPath(info);
   }
   
   public static String getResource(ClassInfo info) {
      URL url = info.url();
      String location = String.valueOf(url).toLowerCase();
      
      if(location.startsWith("jar:file")) {
         try {
            JarURLConnection connection = (JarURLConnection)url.openConnection();
            URL jarUrl = connection.getJarFileURL();
            File file = new File(jarUrl.toURI());
            
            return file.getCanonicalFile().getName();
         } catch(Throwable e) {}
      }
      return getFullPath(info);
   }
   
   public static String getFullPath(ClassInfo info) {
      String path = info.getName();
      
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
   
   public static String getFullPath(Class type) {
      return type.getCanonicalName().replace('.', '/') + ".class";
   }
   
   public static ClassInfo getClassInfo(Class type) {
      String path = ClassReflectionIndexer.getFullPath(type);
      
      try {
         return ClassPathBootstrapScanner.getClassInfo(path);
      }catch(Throwable cause) {
         cause.printStackTrace();
      }
      return null;
   }
   
   public static IndexNode getIndexNode(Class type) {
      ClassInfo info = getClassInfo(type);
      return getIndexNode(info);
   }
   
   public static IndexNode getIndexNode(ClassInfo info) {
      return new ClassIndexNode(info);
   }

   public static IndexNode getIndexNode(Method method) {
      return new MethodIndexNode(method);
   }
   
   public static IndexNode getIndexNode(Field field) {
      return new FieldIndexNode(field);
   }
   
   public static String getFullName(ClassInfo info) {
      String path = info.getResourceName();
      
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
   
   public static String getTypeName(ClassInfo info) {
      String name = getFullName(info);
      int index = name.lastIndexOf('.');
      
      if(index != -1) {
         return name.substring(index + 1);
      }
      return name;
   }
   
   public static String getName(ClassInfo info) {
      String name = getTypeName(info);
      int index = name.lastIndexOf('$');
      
      if(index != -1) {
         return name.substring(index + 1);
      }
      return name;
   }
   
   public static String getModule(ClassInfo info) {
      String name = getFullName(info);
      int index = name.lastIndexOf('.');
      
      if(index != -1) {
         return name.substring(0, index);
      }
      return name;
   }
   
}
