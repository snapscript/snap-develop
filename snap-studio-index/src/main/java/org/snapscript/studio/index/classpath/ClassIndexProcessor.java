package org.snapscript.studio.index.classpath;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.snapscript.studio.index.IndexNode;

import com.google.common.reflect.ClassPath.ResourceInfo;

public class ClassIndexProcessor {
   
   private static final String[] RESOURCE_EXTENSIONS = {
      ".java",
      ".class"
   };
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

   public static ClassFile getClassFile(String path) throws Exception {
      for(String extension : RESOURCE_EXTENSIONS) {
         if (path.endsWith(extension)) {            
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            return new ResourcePathClassFile(path, loader);
         }
      }
      return null;
   }
   
   public static ClassFile getClassFile(Class type) {
      String path = ClassIndexProcessor.getFullPath(type);
      
      try {
         return getClassFile(path);
      }catch(Throwable cause) {
         cause.printStackTrace();
      }
      return null;
   }
   
   public static Set<IndexNode> getChildren(ClassFile info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      
      nodes.addAll(getSupers(info));
      nodes.addAll(getConstructors(info));
      nodes.addAll(getMethods(info));
      nodes.addAll(getFields(info));
      nodes.addAll(getInnerClasses(info));
      
      return nodes;
   }
   
   public static Set<IndexNode> getSupers(ClassFile info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      Set<Class> done = new HashSet<Class>();
      
      try{
         Class type = info.loadClass();
         Set<Class> hierarchy = getHierarchy(type, done);
         
         hierarchy.remove(type);
         
         for(Class entry : hierarchy) {
            IndexNode node = getSuperIndexNode(entry);
            nodes.add(node);
         }
      }catch(Throwable cause) {
         //cause.printStackTrace();
      }
      return nodes;
   }
   
   private static Set<Class> getHierarchy(Class type, Set<Class> types) {
      Set<Class> nodes = new HashSet<Class>();
      
      try{
         if(nodes.add(type)) {
            Set<Class> superAndInterfaces = getSuperTypeAndInterfaces(type);
            
            for(Class baseNode : superAndInterfaces) {
               if(nodes.add(baseNode)) {
                  nodes.addAll(getHierarchy(baseNode, nodes));
               }
            }
            
         }
      }catch(Throwable cause) {
         //cause.printStackTrace();
      }
      return nodes;
   }
   
   private static Set<Class> getSuperTypeAndInterfaces(Class type) {
      Set<Class> nodes = new HashSet<Class>();
      
      try {
         Class superType = type.getSuperclass();
         Class[] declaredInterfaces = type.getInterfaces();
         
         for(Class interfaceType : declaredInterfaces) {
            nodes.add(interfaceType);
         }
         if(superType != null){
            nodes.add(superType);  
         }
      }catch(Throwable cause) {
         //cause.printStackTrace();
      }
      return nodes;
   }

   public static Set<IndexNode> getConstructors(ClassFile info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      
      try{
         Class type = info.loadClass();
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
   
   public static Set<IndexNode> getMethods(ClassFile info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      
      try{
         Class type = info.loadClass();
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
   
   public static Set<IndexNode> getFields(ClassFile info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      
      try{
         Class type = info.loadClass();
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
   
   public static Set<IndexNode> getInnerClasses(ClassFile info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      
      try{
         Class type = info.loadClass();
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

   public static String getFullPath(Class type) {
      return type.getCanonicalName().replace('.', '/') + ".class";
   }
   
   public static IndexNode getIndexNode(Class type) {
      ClassFile info = getClassFile(type);
      return getIndexNode(info);
   }
   
   public static IndexNode getSuperIndexNode(Class type) {
      ClassFile info = getClassFile(type);
      return new SuperIndexNode(info);
   }

   public static IndexNode getSuperIndexNode(ClassFile info) {
      return new SuperIndexNode(info);
   }
   
   public static IndexNode getIndexNode(ClassFile info) {
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

   
}
