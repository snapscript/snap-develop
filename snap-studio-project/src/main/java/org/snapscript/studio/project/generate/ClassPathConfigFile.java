package org.snapscript.studio.project.generate;

import java.util.Collections;
import java.util.List;

import org.snapscript.studio.project.ClassPathFile;

public class ClassPathConfigFile implements ConfigFile, ClassPathFile {

   private final List<String> errors;
   private final String path;
   
   public ClassPathConfigFile(String path) {
      this(path, Collections.EMPTY_LIST);
   }
   
   public ClassPathConfigFile(String path, List<String> errors) {
      this.path = path;
      this.errors = errors;
   }
   
   @Override
   public String getPath(){
      return path;
   }
   
   @Override
   public List<String> getErrors() {
      return errors;
   }

   @Override
   public String getConfigSource() {
      return path;
   }
}
