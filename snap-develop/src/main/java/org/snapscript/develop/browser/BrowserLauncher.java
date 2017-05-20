package org.snapscript.develop.browser;

import java.io.File;

import lombok.AllArgsConstructor;

import org.snapscript.agent.log.ProcessLogger;

@AllArgsConstructor
public class BrowserLauncher {

   private final ProcessLogger logger;
   private final File directory;
   private final boolean disabled;
   private final boolean debug;
   
   public void launch(final String host, final int port) {
      if(!disabled) {
         final BrowserContext context = new BrowserContext.Builder()
            .withDebug(debug)
            .withHost(host)
            .withPort(port)
            .withLogger(logger)
            .withDirectory(directory)
            .build();
         
         final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
               BrowserApplication.launch(context);
            }
         });
         thread.start();
      }
   }
}
