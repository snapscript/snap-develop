package org.snapscript.develop.resource.loader;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassPathResourceLoader {
   
   private final Map<String, byte[]> cache;
   private final ClassPathParser parser;
   private final ClassPathFilter filter;
   
   public ClassPathResourceLoader(List<String> prefix) {
      this.cache = new ConcurrentHashMap<String, byte[]>();
      this.parser = new ClassPathParser();
      this.filter = new ClassPathFilter(prefix);
   }

   public byte[] loadResource(String path) throws Exception {
      byte[] data = cache.get(path);
      
      if(data == null) {
         String type = parser.parse(path);
         
         if(filter.accept(type)) {
            data = findResource(path);
            
            if(data != null) {
               cache.put(path, data);
            }
         }
      }
      return data;
   }
   
   public static byte[] findResource(String path) throws Exception {
      String location = path;
      
      if(location.startsWith("/")) {
         location = path.substring(1);
      }
      ClassLoader loader = ClassLoader.getSystemClassLoader();
      InputStream input = loader.getResourceAsStream(location);
      
      if(input != null) {
         ByteArrayOutputStream output = new ByteArrayOutputStream();
         byte[] buffer = new byte[1024];
         int count = 0;
         
         try {
            while((count = input.read(buffer)) != -1) {
               output.write(buffer, 0, count);
            }
            output.close();
         }finally {
            input.close();
         }
         return output.toByteArray();
      }
      return null;
   }
}