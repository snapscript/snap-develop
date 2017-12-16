package org.snapscript.studio.cli;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class ScriptClassLoader {
   
   private static final String ADD_URL_METHOD = "addURL";
   
   public static void update(File dependencies) throws Exception {
      if(!dependencies.exists()) {
         throw new IllegalArgumentException("Could not find classpath file " + dependencies);
      }
      if(dependencies.isFile()) {
         FileReader source = new FileReader(dependencies);
         LineNumberReader reader = new LineNumberReader(source);
         List<File> files = new ArrayList<File>();
         
         try {
            URLClassLoader loader = (URLClassLoader)ClassLoader.getSystemClassLoader();
            Method method = URLClassLoader.class.getDeclaredMethod(ADD_URL_METHOD, URL.class);
            String line = reader.readLine();
            
            while(line != null) {
               String token = line.trim();
               int length = token.length();
               
               if(length > 0) {
                  File file = new File(token);
                  files.add(file);
               }
               line = reader.readLine();
            }
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
         } finally {
            reader.close();
         }
      }
   }
}