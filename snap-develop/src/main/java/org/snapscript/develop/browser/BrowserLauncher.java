package org.snapscript.develop.browser;

import java.io.File;

import org.snapscript.develop.CommandLineArgument;

public class BrowserLauncher {

   private final boolean enabled;
   
   public BrowserLauncher(boolean enabled) {
      this.enabled = enabled;
   }
   
   public void launch(String host, int port) {
      if(enabled) {
         String directory = CommandLineArgument.DIRECTORY.getValue();
         File file = new File(directory);
         BrowserApplication.launch(file, host, port);
      }
   }
}
