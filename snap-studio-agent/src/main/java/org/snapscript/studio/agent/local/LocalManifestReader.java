package org.snapscript.studio.agent.local;

import java.io.InputStream;
import java.util.jar.Manifest;

public class LocalManifestReader {

   public static Manifest readManifest() {
      try {
         InputStream resource = LocalManifestReader.class.getResourceAsStream("META-INF/MANIFEST.MF");
         
         if(resource == null) {
            resource = LocalManifestReader.class.getResourceAsStream("/META-INF/MANIFEST.MF");
         }
         return new Manifest(resource);
      } catch(Exception e) {
         throw new IllegalStateException("Could not read manifest file", e);
      }
   }
}
