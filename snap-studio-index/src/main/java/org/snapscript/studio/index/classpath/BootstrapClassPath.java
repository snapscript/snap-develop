package org.snapscript.studio.index.classpath;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.snapscript.studio.index.IndexNode;

import com.google.common.reflect.ClassPath.ClassInfo;

public class BootstrapClassPath {

   private static final Set<IndexNode> BOOTSTRAP_CLASSES;
   private static final Map<String, IndexNode> DEFAULT_IMPORT_CLASSES;
   private static final String IGNORE_PREFIX = "java.";
   private static final String[] DEFAULT_IMPORTS = {
      "java.lang.",
      "java.util.",
      "java.net.",
      "java.io."
   };
   private static final String[] RUNTIME_JAR_PATHS = {
      "/jre/lib/rt.jar",
      "/lib/rt.jar"
   };
   
   static {
      try {
         DEFAULT_IMPORT_CLASSES = new ConcurrentHashMap<String, IndexNode>();
         BOOTSTRAP_CLASSES = new CopyOnWriteArraySet<IndexNode>();
      } catch (Throwable e) {
         throw new ExceptionInInitializerError(e);
      }
   }
   
   public static Set<IndexNode> getBootstrapClasses() {
      if(BOOTSTRAP_CLASSES.isEmpty()) {
         try {
            String javaHome = System.getProperty("java.home");
            
            for(String path : RUNTIME_JAR_PATHS) {
               File file = new File(javaHome, path);
               
               if(file.exists()) {
                  String location = file.getCanonicalPath();
                  findClassesInJar(BOOTSTRAP_CLASSES, location);
                  break;
               }
            }
         } catch (Throwable e) {
            return Collections.emptySet();
         }
      }
      return BOOTSTRAP_CLASSES;
   }
   
   public static Map<String, IndexNode> getDefaultImportClasses() {
      if(DEFAULT_IMPORT_CLASSES.isEmpty()) {
         Set<IndexNode> nodes = getBootstrapClasses();

         for(IndexNode node : nodes) {
            String shortName = node.getName();
            String fullName = node.getFullName();
            
            for(String prefix : DEFAULT_IMPORTS) {
               if(fullName.startsWith(IGNORE_PREFIX)) {
                  if(fullName.startsWith(prefix) && fullName.equals(prefix + shortName)) {
                     DEFAULT_IMPORT_CLASSES.put(shortName, node);
                  }
                  int length = IGNORE_PREFIX.length();
                  String alias = fullName.substring(length);
                  
                  DEFAULT_IMPORT_CLASSES.put(alias, node);
                  DEFAULT_IMPORT_CLASSES.put(fullName, node);
               }
            }
         }
      }
      return DEFAULT_IMPORT_CLASSES;
   }

   private static void findClassesInJar(Set<IndexNode> classFiles, String path) throws Exception {
      JarFile jarFile = new JarFile(path);

      try {
         Enumeration<JarEntry> entries = jarFile.entries();

         while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String location = entry.getName();
            ClassInfo info = ClassIndexProcessor.getClassInfo(location);
            
            if (info != null) {
               IndexNode node = ClassIndexProcessor.getIndexNode(info);
               classFiles.add(node);
            }
         }
      } finally {
         jarFile.close();
      }
   }

}
