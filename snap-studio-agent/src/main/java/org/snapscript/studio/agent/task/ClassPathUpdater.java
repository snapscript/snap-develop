package org.snapscript.studio.agent.task;

import java.io.File;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class ClassPathUpdater {
   
   private static final String ADD_URL_METHOD = "addURL";
   
   public static List<File> parseClassPath(String dependencies) throws Exception {
      Reader source = new StringReader(dependencies);
      LineNumberReader reader = new LineNumberReader(source);
      List<File> files = new ArrayList<File>();
      
      try {
         String line = reader.readLine();
         
         while(line != null) {
            String token = line.trim();
            int length = token.length();
            
            if(length > 0) {
               if(!token.startsWith("#")) {
                  File file = new File(token);
                  files.add(file);
               }
            }
            line = reader.readLine();
         }
      } finally {
         reader.close();
      }
      return files;
   }
   
   public static ClassLoader updateClassPath(String dependencies) throws Exception {  
      ClassLoader loader = ClassLoader.getSystemClassLoader();
      
      if(URLClassLoader.class.isInstance(loader)) { // could be Android PathClassLoader
         Method method = URLClassLoader.class.getDeclaredMethod(ADD_URL_METHOD, URL.class);
         List<File> files = parseClassPath(dependencies);
         int size = files.size();
         
         if(size > 0) {
            for(int i = 0; i < size; i++){
               File file = files.get(i);
               URI location = file.toURI();
               URL path = location.toURL();
               
               method.setAccessible(true);
               method.invoke(loader, path);
            } 
         }
         return loader;
      }
      return null;
   }
}
