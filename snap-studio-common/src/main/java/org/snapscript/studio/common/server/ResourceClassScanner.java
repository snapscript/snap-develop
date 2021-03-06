package org.snapscript.studio.common.server;

import static org.springframework.core.io.support.ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX;

import javax.ws.rs.Path;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.snapscript.core.Any;
import org.snapscript.core.ContextClassLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ResourceClassScanner {
   
   private static final String RESOURCE_SUFFIX = "Resource";
   private static final String RESOURCE_PACKAGE = "org.snapscript.";
   private static final String RESOURCE_PATH = "org/snapscript/";
   
   private final PathMatchingResourcePatternResolver resolver;
   private final ClassLoader loader;
   
   public ResourceClassScanner() {
      this.resolver = new PathMatchingResourcePatternResolver(); 
      this.loader = new ContextClassLoader(Any.class);
   }

   public Set<Class<?>> scan() {
      try {
         String pattern = CLASSPATH_ALL_URL_PREFIX + RESOURCE_PATH + "**/*" + RESOURCE_SUFFIX + ".class";
         Resource[] resources = resolver.getResources(pattern);
         
         log.debug("Scan of '{}' found {} resources", pattern, resources.length);
         
         if(resources.length > 0) {
            Set<Class<?>> matches = new HashSet<Class<?>>();
            
            for(Resource resource : resources) {
               URL target = resource.getURL();
               String location = target.toString();
               String name = resolve(location); // remove .class and path prefix
               
               if(name.startsWith(RESOURCE_PACKAGE)) {
                  try {
                     Class<?> type = loader.loadClass(name);
                     Path annotation = type.getAnnotation(Path.class);
                     
                     if(annotation != null) {                        
                        String value = annotation.value();
                        
                        log.debug("Loading resource {}", resource);
                        matches.add(type);
                     } else {
                        log.debug("Ignoring resource {}", resource);
                     }
                  } catch(Exception e) {
                     log.warn("Could not load {}", name);
                  }
               }
            }
            return matches;
         }
      } catch(Exception e) {
         throw new IllegalStateException("Could not scan for resources", e);
      }
      return Collections.emptySet();
   }
   
   private String resolve(String location) {
      try {
         int separator = location.lastIndexOf("!"); // if its a jar resource
         
         if(separator != -1) {
            location = location.substring(separator + 1);
         }
         String normal = location.replace('/', '.');
         int index = normal.lastIndexOf(RESOURCE_PACKAGE);
         int length = normal.length();
         
         return normal.substring(index, length - 6); // remove .class and path prefix
      } catch(Exception e) {
         throw new IllegalStateException("Could parse " + location, e);
      }
   }
}
