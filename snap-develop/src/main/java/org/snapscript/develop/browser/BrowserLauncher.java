package org.snapscript.develop.browser;

import java.io.File;

import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.develop.CommandLineArgument;

public class BrowserLauncher {

   private final ProcessLogger logger;
   private final boolean enabled;
   
   public BrowserLauncher(ProcessLogger logger, boolean enabled) {
      this.logger = logger;
      this.enabled = enabled;
   }
   
   public void launch(final String host, final int port) {
      if(enabled) {
         final String directory = CommandLineArgument.DIRECTORY.getValue();
         final File file = new File(directory);
         final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
               BrowserApplication.launch(logger, file, host, port);
            }
         });
         thread.start();
      }
   }
}
