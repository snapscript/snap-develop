
package org.snapscript.develop.resource.loader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class JarFileBuilder {

   private final ClassPathResourceLoader loader;
   
   public JarFileBuilder(ClassPathResourceLoader loader) {
      this.loader = loader;
   }
   
   public JarFile create() {
      Manifest manifest = new Manifest();
      return new ClassPathJarFile(manifest);
   }
   
   public JarFile create(String mainClass) {
      Manifest manifest = new Manifest();
      manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
      manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, mainClass);
      return new ClassPathJarFile(manifest);
   }
   
   private class ClassPathJarFile implements JarFile {
      
      private final Map<String, byte[]> resources;
      private final Manifest manifest;
      
      public ClassPathJarFile(Manifest manifest) {
         this.resources = new LinkedHashMap<String, byte[]>();
         this.manifest = manifest;
      }
      
      @Override
      public JarFile saveFile(File file) throws Exception {
         File parent = file.getParentFile();
         
         if(!parent.exists()) {
            parent.mkdirs();
         }
         if(file.exists()) {
            file.delete();
         }
         OutputStream stream = new FileOutputStream(file);
         
         try {
            return saveFile(stream);
         }finally {
            stream.close();
         }
      }
      
      @Override
      public JarFile saveFile(OutputStream stream) throws Exception {
         JarOutputStream out = new JarOutputStream(stream, manifest);
   
         try {
            Set<String> keys = resources.keySet();
            
            for(String key : keys) {
               JarEntry entry = createJarEntry(key);
               byte[] data = resources.get(key);
               
               out.putNextEntry(entry);
               out.write(data);
            }
         } finally {
            out.close();
         }
         return this;
      }
      
      @Override
      public JarFile addResource(String resource) throws Exception {
         byte[] data = loader.loadResource(resource);
         
         if(data == null) {
            throw new IllegalStateException("Could not fine resource " + resource);
         }
         
         resources.put(resource, data);
         return this;
      }
      
      @Override
      public JarFile addManifestAttribute(Name name, String value) throws Exception {
         manifest.getMainAttributes().put(name, value);
         return this;
      }
      
      @Override
      public JarFile addManifestAttribute(String attribute, String value) throws Exception {
         Name name = new Name(attribute);
         manifest.getMainAttributes().put(name, value);
         return this;
      }
      
      private JarEntry createJarEntry(String resource) throws Exception {
         String path = resource;
         
         if(path.startsWith("/")) {
            path = resource.substring(1); // /org/domain/Type.class -> org/domain/Type.class
         }
         return new JarEntry(path);
      }
   }
}
