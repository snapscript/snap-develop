package org.snapscript.studio.resource.loader;

import static org.snapscript.studio.configuration.Configuration.CLASS_EXTENSION;

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
         
         if(path.endsWith(CLASS_EXTENSION)) {
            type = path.substring(1, length -6);
            type = type.replace('/', '.');
            
            cache.put(path, type);
         } else {
            type = path;
            cache.put(path, type);
         }
            
      }
      return type;
   }
}