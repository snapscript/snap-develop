package org.snapscript.develop.http.loader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassPathParser {
   
   private final Map<String, String> cache;
   
   public ClassPathParser() {
      this.cache = new ConcurrentHashMap<String, String>();
   }

   public String parse(String path) {
      String type = cache.get(path);
      
      if(type == null) {
         int length = path.length();
         
         if(length > 7) {
            type = path.substring(1, length -6);
            type = type.replace('/', '.');
            
            cache.put(path, type);
         }
      }
      return type;
   }
}
