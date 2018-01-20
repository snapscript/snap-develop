package org.snapscript.studio.ui.swt;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChromiumLoader {

   private static final AtomicBoolean loaded = new AtomicBoolean();

   public static void loadLib(File installPath) {
      if (loaded.compareAndSet(false, true)) {
         if (installPath == null) {
            installPath = new File(System.getProperty("user.home"), ".snap");
         }
         try {
            String installDir = installPath.getCanonicalPath();
            if (!installPath.exists()) {
               throw new IllegalArgumentException("Could not find install path " + installDir);
            }
            // Chromium.loadLib(installDir);
         } catch (Exception e) {
            throw new IllegalArgumentException("Could not find install path " + installPath, e);
         }
      }
   }

   public static void ensureLibLoaded() {
      loadLib(null);
   }
}
