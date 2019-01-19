package org.snapscript.studio.service;

public class ProgressManager {

   public static ProgressListener getProgress() {
      return SplashScreen.getPanel();
   }
}
