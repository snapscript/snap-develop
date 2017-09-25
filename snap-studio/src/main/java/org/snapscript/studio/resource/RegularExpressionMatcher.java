package org.snapscript.studio.resource;

import static org.simpleframework.http.Method.CONNECT;

import java.util.Map;
import java.util.Set;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.common.LeastRecentlyUsedMap;
              
public class RegularExpressionMatcher implements ResourceMatcher {

   private final Map<String, Resource> resources;
   private final Map<String, Resource> cache;
   private final Resource fallback;

   public RegularExpressionMatcher(Map<String, Resource> resources) {
      this(resources, null);
   }
   
   public RegularExpressionMatcher(Map<String, Resource> resources, Resource fallback) {
      this(resources, fallback, 10000);
   }

   public RegularExpressionMatcher(Map<String, Resource> resources, Resource fallback, int capacity) {
      this.cache = new LeastRecentlyUsedMap<String, Resource>(capacity);
      this.resources = resources;
      this.fallback = fallback;
   }

   @Override
   public synchronized Resource match(Request request, Response response) {
      Path path = request.getPath();
      String target = path.getPath();
      String method = request.getMethod();
      
      if(method.equals(CONNECT)) { // connect uses domain:port rather than path
         target = request.getTarget();
      }
      Resource resource = cache.get(target);

      if (resource == null) {
         resource = match(request, target);

         if (resource != null) {
            cache.put(target, resource);
         }
      }
      return resource;
   }

   private synchronized Resource match(Request request, String target) {
      Set<String> mappings = resources.keySet();

      for (String mapping : mappings) {
         Resource resource = resources.get(mapping);

         if (target.matches(mapping)) {
            return resource;
         }
      }
      return fallback;
   }
}