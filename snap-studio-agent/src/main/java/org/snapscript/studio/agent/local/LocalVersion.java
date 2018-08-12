package org.snapscript.studio.agent.local;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class LocalVersion {

   public static final String IMPLEMENTATION_VERSION = "Implementation-Version";
   public static final String DEFAULT_VERSION = "1.0";

   public static String getVersion() {
      Attributes.Name key = new Attributes.Name(IMPLEMENTATION_VERSION);
      Manifest manifest = LocalManifestReader.readManifest();
      String version = (String)manifest.getMainAttributes().get(key);

      return version == null ? DEFAULT_VERSION : version;
   }
}
