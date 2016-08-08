package org.snapscript.develop.http.resource;

import java.io.IOException;
import java.io.InputStream;

public class FileManager {

   private final String encoding;
   private final String base;

   public FileManager(String base) {
      this(base, "UTF-8");
   }
   
   public FileManager(String base, String encoding) {
      this.encoding = encoding;
      this.base = base;
   }

   public InputStream openInputStream(String path) throws IOException {
      String root = base;
      
      if(!root.startsWith("/")) {
         root = "/" + root;
      }
      if(path.startsWith("/")) {
         path = path.substring(1);
      }
      if(root.endsWith("/")) {
         return FileManager.class.getResourceAsStream(root +path);
      }
      return FileManager.class.getResourceAsStream(root + "/" +path);
   }
}
