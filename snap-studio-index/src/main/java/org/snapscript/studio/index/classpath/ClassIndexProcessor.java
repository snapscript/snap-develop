package org.snapscript.studio.index.classpath;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;

import org.snapscript.studio.index.IndexNode;

import com.google.common.reflect.ClassPath.ClassInfo;
import com.google.common.reflect.ClassPath.ResourceInfo;

public class ClassIndexProcessor {
   
   private static final Method RESOURCE_METHOD;

   static  {
      try {
         RESOURCE_METHOD = ResourceInfo.class.getDeclaredMethod("of", String.class, ClassLoader.class);
   
         if (!RESOURCE_METHOD.isAccessible()) {
            RESOURCE_METHOD.setAccessible(true);
         }
      } catch (Throwable e) {
         throw new ExceptionInInitializerError(e);
      }
   }
   
   public static IndexNode getDefaultImport(String name) {
      return BootstrapClassPath.getDefaultImportClasses().get(name);
   }

   public static ClassInfo getClassInfo(String path) throws Exception {
      if (path.endsWith(".class") && path.startsWith("java")) {
         ClassLoader loader = Thread.currentThread().getContextClassLoader();
         return (ClassInfo) RESOURCE_METHOD.invoke(null, path, loader);
      }
      return null;
   }
   
   public static ClassInfo getClassInfo(Class type) {
      String path = ClassIndexProcessor.getFullPath(type);
      
      try {
         return getClassInfo(path);
      }catch(Throwable cause) {
         cause.printStackTrace();
      }
      return null;
   }
   
   public static Set<IndexNode> getChildren(ClassInfo info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      
      nodes.addAll(getSupers(info));
      nodes.addAll(getConstructors(info));
      nodes.addAll(getMethods(info));
      nodes.addAll(getFields(info));
      nodes.addAll(getInnerClasses(info));
      
      return nodes;
   }
   
   public static Set<IndexNode> getSupers(ClassInfo info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      
      try{
         Class type = info.load();
         Class superType = type.getSuperclass();
         Class[] declaredInterfaces = type.getInterfaces();
         
         for(Class declaredInterface : declaredInterfaces) {
            IndexNode node = getSuperIndexNode(declaredInterface);
            nodes.add(node);
         }
         IndexNode node = getSuperIndexNode(superType);
         nodes.add(node);
      }catch(Throwable cause) {
         //cause.printStackTrace();
      }
      return nodes;
   }

   public static Set<IndexNode> getConstructors(ClassInfo info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      
      try{
         Class type = info.load();
         Constructor[] constructors = type.getDeclaredConstructors();
         
         for(Constructor constructor : constructors) {
            IndexNode node = getIndexNode(constructor);
            nodes.add(node);
         }
      }catch(Throwable cause) {
         //cause.printStackTrace();
      }
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
         //cause.printStackTrace();
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
         //cause.printStackTrace();
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
         //cause.printStackTrace();
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
   
   public static IndexNode getIndexNode(Class type) {
      ClassInfo info = getClassInfo(type);
      return getIndexNode(info);
   }
   
   public static IndexNode getSuperIndexNode(Class type) {
      ClassInfo info = getClassInfo(type);
      return new SuperIndexNode(info);
   }

   public static IndexNode getSuperIndexNode(ClassInfo info) {
      return new SuperIndexNode(info);
   }
   
   public static IndexNode getIndexNode(ClassInfo info) {
      return new ClassIndexNode(info);
   }

   public static IndexNode getIndexNode(Constructor constructor) {
      return new ConstructorIndexNode(constructor);
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
