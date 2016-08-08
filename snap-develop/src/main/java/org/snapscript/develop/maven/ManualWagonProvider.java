package org.snapscript.develop.maven;

import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.providers.http.LightweightHttpWagon;
import org.sonatype.aether.connector.wagon.WagonProvider;

public class ManualWagonProvider implements WagonProvider {
   
   private static final String SCHEME = "http";

   @Override
   public Wagon lookup(String roleHint) throws Exception {
      if (SCHEME.equals(roleHint)) {
         return new LightweightHttpWagon();
      }
      return null;
   }

   @Override
   public void release(Wagon wagon) {

   }

}