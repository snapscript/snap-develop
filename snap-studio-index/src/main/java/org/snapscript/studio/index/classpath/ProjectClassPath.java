package org.snapscript.studio.index.classpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.snapscript.studio.index.IndexNode;

import com.google.common.reflect.ClassPath.ClassInfo;

public class ProjectClassPath {

   public static IndexPath getSystemClassPath() {
      ClassLoader loader = ClassLoader.getSystemClassLoader();
      List<ClassFile> files = ProjectClassPath.getClassFiles(loader);
      return new IndexPath(loader, files, null);
   }
   
   public static Set<IndexNode> getProjectClassPath(IndexPath path) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      
      try {
         List<ClassFile> projectClasses = path.getClassFiles();
      
         for(ClassFile file : projectClasses) {
            IndexNode node = ClassIndexProcessor.getIndexNode(file);
            nodes.add(node);
         }
      } catch(Throwable e) {
         //e.printStackTrace();
      }
      nodes.addAll(BootstrapClassPath.getBootstrapClasses());
      return Collections.unmodifiableSet(nodes);
   }
   
   
   public static List<ClassFile> getClassFiles(ClassLoader loader) {
      try {
         Set<ClassInfo> projectClasses = com.google.common.reflect.ClassPath.from(loader).getAllClasses();
         
         if(!projectClasses.isEmpty()) {
            List<ClassFile> files = new ArrayList<ClassFile>();
         
            for(ClassInfo info : projectClasses) {
               String path = info.getResourceName();
               
               if(!isTargetResource(path)) {
                  String resource = info.getResourceName();
                  ClassFile file = new ResourcePathClassFile(resource, loader);
                  files.add(file);
               }
            }
            return Collections.unmodifiableList(files);
         }
      } catch(Throwable e) {
         //e.printStackTrace();
      }
      return Collections.emptyList();
   }
   
   private static boolean isTargetResource(String resourcePath) {
      return resourcePath.startsWith("target/");
   }
}
