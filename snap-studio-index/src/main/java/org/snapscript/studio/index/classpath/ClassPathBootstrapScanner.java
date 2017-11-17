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
         Method method = ResourceInfo.class.getDeclaredMethod("of", String.class, ClassLoader.class);

         if (!method.isAccessible()) {
            method.setAccessible(true);
         }
         while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();

            if (entryName.endsWith(".class") && entryName.startsWith("java")) {
               ClassLoader loader = Thread.currentThread().getContextClassLoader();
               ClassInfo info = (ClassInfo) method.invoke(null, entryName, loader);

               classFiles.add(info);
            }
         }
      } finally {
         jarFile.close();
      }
   }
}
