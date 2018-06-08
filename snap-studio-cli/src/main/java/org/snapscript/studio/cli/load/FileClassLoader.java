package org.snapscript.studio.cli.load;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.snapscript.studio.cli.CommandLineUsage;

public class FileClassLoader {
   
   private static final String WARNING = "Could not find classpath entry %s";
   private static final String INCLUDE_MESSAGE = "Including %s in classpath";
   private static final String ADD_URL_METHOD = "addURL";
   private static final String JAR_EXTENSION = ".jar";
   
   public static void update(List<File> dependencies, boolean debug) throws Exception {
      for(File dependency : dependencies) {
         if(!dependency.exists()) {
            String warning = String.format(WARNING, dependency);
            CommandLineUsage.usage(warning);
         }
      }
      URLClassLoader loader = (URLClassLoader)ClassLoader.getSystemClassLoader();
      Method method = URLClassLoader.class.getDeclaredMethod(ADD_URL_METHOD, URL.class);
      
      for(File dependency : dependencies) {
         String resource = dependency.getAbsolutePath();
         
         if(dependency.isFile() && !resource.endsWith(JAR_EXTENSION)) {
            FileReader source = new FileReader(dependency);
            LineNumberReader reader = new LineNumberReader(source);
            List<File> files = new ArrayList<File>();
            
            try {
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
                     File file = files.get(i).getCanonicalFile();
                     URI location = file.toURI();
                     URL path = location.toURL();
                     
                     if(debug) {
                        String message = String.format(INCLUDE_MESSAGE , path);
                        System.err.println(message);
                     }
                     if(!file.exists()) {
                        throw new IllegalArgumentException("Could not find " + path);
                     }
                     method.setAccessible(true);
                     method.invoke(loader, path);
                  } 
               }
            } finally {
               reader.close();
            }
         } else {
            File file = dependency.getCanonicalFile();
            URI location = file.toURI();
            URL path = location.toURL();
            
            if(debug) {
               String message = String.format(INCLUDE_MESSAGE , path);
               System.err.println(message);
            }
            if(!file.exists()) {
               throw new IllegalArgumentException("Could not find " + path);
            }
            method.setAccessible(true);
            method.invoke(loader, path);
         }
      }
   }
}