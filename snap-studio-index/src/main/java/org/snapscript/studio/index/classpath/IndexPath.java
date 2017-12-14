package org.snapscript.studio.index.classpath;

import java.util.List;

public class IndexPath {
   
   private final List<ClassFile> files;
   private final ClassLoader loader;
   private final String text;
   
   public IndexPath(ClassLoader loader, List<ClassFile> files, String text) {
      this.loader = loader;
      this.files = files;
      this.text = text;
   }
   
   public ClassLoader getClassLoader() {
      return loader;
   }
   
   public List<ClassFile> getClassFiles() {
      return files;
   }
   
   public String getText(){
      return text;
   }
}
