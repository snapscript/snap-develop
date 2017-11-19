package org.snapscript.studio.common;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.snapscript.core.Bug;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class ClassPathResourceLoader {
   
   private final Map<String, byte[]> cache;
   private final ClassPathParser parser;
   private final ClassPathFilter filter;
   
   @Bug("What is going on here??")
   public ClassPathResourceLoader(@Value("${loader.prefixes:org.snapscript.,/grammar.txt,/import.txt}") String[] prefix, @Value("${loader.extension:.class}") String extension) {
      this.cache = new ConcurrentHashMap<String, byte[]>();
      this.parser = new ClassPathParser(extension);
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
      InputStream input = findResourceAsStream(path);
      
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
   
   public static InputStream findResourceAsStream(String path) throws Exception {
      String location = path;
      
      if(location.startsWith("/")) {
         location = path.substring(1);
      }
      return new ClassPathResource(location, ClassPathResource.class.getClassLoader()).getInputStream();
   }
}