package org.snapscript.studio.index.classpath;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.JarFile;

public class ResourcePathClassFile implements ClassFile {
   
   private ClassLoader loader;
   private String fullName;
   private String absolute;
   private String location;
   private URL url;
   private String path;
   
   public ResourcePathClassFile(String path, ClassLoader loader) {
      this.loader = loader;
      this.path = path;
   }
   
   @Override
   public URL getURL(){
      try {
         if(url == null){
            url = loader.getResource(path);
         }
      }catch(Throwable e){
         return null;
      }
      return url;
   }
   
   @Override
   public Class loadClass() {
      try {
         String path = getFullName();
         return loader.loadClass(path);
      } catch(Throwable e) {
         return null;
      }
   }
   
   @Override
   public String getFullName() {
      if(fullName == null) {
         String path = getResourceName();
         
         if(path.startsWith("/") || path.startsWith("\\")) {
            path = path.substring(1);
         }
         int length = path.length();
         int extension = ".class".length();
         
         path = path.substring(0, length - extension);
         path = path.replace('/', '.');
         path = path.replace('\\', '.');
         
         fullName = path;
      }
      return fullName;
   }
   
   @Override
   public String getTypeName() {
      String name = getFullName();
      int index = name.lastIndexOf('.');
      
      if(index != -1) {
         return name.substring(index + 1);
      }
      return name;
   }
   
   @Override
   public String getName() {
      String name = getTypeName();
      int index = name.lastIndexOf('$');
      
      if(index != -1) {
         return name.substring(index + 1);
      }
      return name;
   }
   
   @Override
   public String getModule() {
      String name = getFullName();
      int index = name.lastIndexOf('.');
      
      if(index != -1) {
         return name.substring(0, index);
      }
      return name;
   }
   
   @Override
   public String getAbsolutePath() {
      if(absolute == null) {
         URL url = getURL();
         String token = String.valueOf(url).toLowerCase();
         
         if(token.startsWith("jar:file")) {
            try {
               JarURLConnection connection = (JarURLConnection) url.openConnection();
               JarFile jarFile = connection.getJarFile();
               File file = new File(jarFile.getName());
               
               absolute = file.getCanonicalPath();
               return absolute;
            } catch(Throwable e) {}
         }
         absolute = getResourceName();
      }
      return absolute;
   }

   @Override
   public String getLocation() {
      if(location == null) {
         URL url = getURL();
         String token = String.valueOf(url).toLowerCase();
         
         if(token.startsWith("jar:file")) {
            try {
               JarURLConnection connection = (JarURLConnection)url.openConnection();
               URL jarUrl = connection.getJarFileURL();
               File file = new File(jarUrl.toURI());
               
               location = file.getCanonicalFile().getName();
               return location;
            } catch(Throwable e) {}
         }
         location = getResourceName();
      }
      return location;
   }
   
   @Override
   public String getResourceName() {
      return path;
   }
}