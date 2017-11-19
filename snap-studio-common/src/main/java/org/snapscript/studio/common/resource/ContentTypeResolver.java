package org.snapscript.studio.common.resource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.snapscript.common.Cache;
import org.snapscript.common.LeastRecentlyUsedCache;
import org.snapscript.core.Bug;
import org.snapscript.studio.common.ClassPathResourceLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class ContentTypeResolver {

   private final Cache<String, String> cache;
   private final Map<String, String> types;
   private final String file;
   private final Gson gson;
   
   @Bug("this is rubbish")
   public ContentTypeResolver(@Value("${content.types:context/types.json}") String file, @Value("${content.cache:1000}") int capacity) {
      this.cache = new LeastRecentlyUsedCache<String, String>(capacity);
      this.types = new ConcurrentHashMap<String, String>();
      this.gson = new Gson();
      this.file = file;
   }
   
   private Map<String, String> readTypes() {
      if(types.isEmpty()) {
         try {
            InputStream stream = ClassPathResourceLoader.findResourceAsStream(file);
            Reader reader = new InputStreamReader(stream, "UTF-8");
            Map<String, String> map = (Map)gson.fromJson(reader, Map.class);
            
            types.putAll(map);
         }catch(Exception e) {
            e.printStackTrace();
         }
      }
      return types;
   }

   public String matchPath(String path) {
      Map<String, String> types = readTypes();
      Set<String> expressions = types.keySet();
      String token = path.toLowerCase();

      for (String expression : expressions) {         
         if (token.equalsIgnoreCase(expression) || token.matches(expression)) {
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