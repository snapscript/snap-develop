package org.snapscript.develop.http.resource;

import java.util.Map;
import java.util.Set;

import org.snapscript.common.Cache;
import org.snapscript.common.LeastRecentlyUsedCache;

public class ContentTypeResolver {

   private final Cache<String, String> cache;
   private final Map<String, String> types;

   public ContentTypeResolver(Map<String, String> types) {
      this(types, 10000);
   }
   
   public ContentTypeResolver(Map<String, String> types, int capacity) {
      this.cache = new LeastRecentlyUsedCache<String, String>(capacity);
      this.types = types;
   }

   public String matchPath(String path) {
      Set<String> expressions = types.keySet();
      String token = path.toLowerCase();

      for (String expression : expressions) {         
         if (token.matches(expression)) {
            String type = types.get(expression);
            
            if(type != null) {             
               return type;
            }
         }
      }
      return "application/octet-stream";
   }   

   public String resolveType(String path) {
      String result = cache.fetch(path);
      
      if(result == null) {
         String type = matchPath(path);
         
         if(type != null) {
            cache.cache(path, type);
            return type;
         }
      }
      return result;
   }
}
