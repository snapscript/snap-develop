package org.snapscript.studio.browser;

import java.io.File;

import lombok.AllArgsConstructor;

import org.snapscript.studio.Workspace;

@AllArgsConstructor
public class BrowserLauncher {

   private final Workspace workspace;
   private final File directory;
   private final boolean disabled;
   private final boolean debug;
   
   public void launch(final String host, final int port) {
      if(!disabled) {
         final BrowserContext context = new BrowserContext.Builder()
            .withDebug(debug)
            .withHost(host)
            .withPort(port)
            .withLogger(workspace.getLogger())
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