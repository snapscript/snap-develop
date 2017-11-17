package org.snapscript.studio.index.classpath;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.google.common.reflect.ClassPath.ClassInfo;
import com.google.common.reflect.ClassPath.ResourceInfo;

public class ClassPathBootstrapScanner {
   
   private static final Method RESOURCE_METHOD;
   
   static {
      try {
      RESOURCE_METHOD = ResourceInfo.class.getDeclaredMethod("of", String.class, ClassLoader.class);

      if (!RESOURCE_METHOD.isAccessible()) {
         RESOURCE_METHOD.setAccessible(true);
      }
      } catch(Throwable e) {
         throw new ExceptionInInitializerError(e);
      }
   }

   public static Set<ClassInfo> getBootstrapClasses() throws Exception {
      Set<ClassInfo> resources = new HashSet<ClassInfo>();
      
      try {
         String javaHome = System.getProperty("java.home");
         File rtFile = new File(javaHome + "/jre/lib/rt.jar");
         
         if (!rtFile.exists()) {
            rtFile = new File(javaHome + "/lib/rt.jar");
         }
         if(rtFile.exists()) {
            String path = rtFile.getCanonicalPath();
            findClassesInJar(resources, path);
         }
      } catch (Throwable e) {
         return Collections.emptySet();
      }
      return resources;
   }

   private static void findClassesInJar(Set<ClassInfo> classFiles, String path) throws Exception {
      JarFile jarFile = new JarFile(path);

      try {
         Enumeration<JarEntry> entries = jarFile.entries();

         while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            ClassInfo info = getClassInfo(entryName);

            if(info != null) {
               classFiles.add(info);
            }
         }
      } finally {
         jarFile.close();
      }
   }
   
   public static ClassInfo getClassInfo(String path) throws Exception {
      if (path.endsWith(".class") && path.startsWith("java")) {
         ClassLoader loader = Thread.currentThread().getContextClassLoader();
         return (ClassInfo) RESOURCE_METHOD.invoke(null, path, loader);
      }
      return null;
   }
}
