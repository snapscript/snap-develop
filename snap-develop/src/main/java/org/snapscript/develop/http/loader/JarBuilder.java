package org.snapscript.develop.http.loader;

import java.io.ByteArrayOutputStream;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class JarBuilder {

   private final ClassResourceLoader loader;
   
   public JarBuilder(ClassResourceLoader loader) {
      this.loader = loader;
   }

   public byte[] createJar(String mainClass, String... resources) throws Exception {
      Manifest manifest = new Manifest();
      manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
      manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, mainClass);
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      JarOutputStream out = new JarOutputStream(stream, manifest);

      for (String resource : resources) {
         String path = resource;
         
         if(path.startsWith("/")) {
            path = resource.substring(1); // /org/domain/Type.class -> org/domain/Type.class
         }
         JarEntry entry = new JarEntry(path);
         byte[] data = loader.loadClass(resource);
         
         if(data == null) {
            throw new IllegalStateException("Could not fine resource " + resource);
         }
         out.putNextEntry(entry);
         out.write(data);
      }
      out.close();
      stream.close();
      return stream.toByteArray();
   }
}
