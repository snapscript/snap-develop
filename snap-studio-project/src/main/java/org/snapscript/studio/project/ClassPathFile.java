package org.snapscript.studio.project;

import java.util.Collections;
import java.util.List;

public class ClassPathFile {

   private final List<String> errors;
   private final String path;
   
   public ClassPathFile(String path) {
      this(path, Collections.EMPTY_LIST);
   }
   
   public ClassPathFile(String path, List<String> errors) {
      this.path = path;
      this.errors = errors;
   }
   
   public String getPath(){
      return path;
   }
   
   public List<String> getErrors() {
      return errors;
   }
}
