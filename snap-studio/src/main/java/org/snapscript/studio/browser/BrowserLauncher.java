package org.snapscript.studio.browser;

import java.io.File;

import org.snapscript.studio.project.Workspace;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BrowserLauncher {

   private final Workspace workspace;
   private final File directory;
   private final boolean disabled;
   private final boolean debug;
   
   public BrowserLauncher(
         Workspace workspace, 
         @Value("${directory}") File directory, 
         @Value("${server-only}") boolean disabled, 
         @Value("${client-debug}") boolean debug)
   {
      this.workspace = workspace;
      this.directory = directory;
      this.disabled = disabled;
      this.debug = debug;
   }
   
   public void launch(final String engine, final String host, final int port) {
      if(!disabled) {
         final BrowserContext context = new BrowserContext.Builder()
            .withDebug(debug)
            .withHost(host)
            .withPort(port)
            .withEngine(engine)
            .withDirectory(directory)
            .build();
         
         final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
               BrowserFactory.createBrowser(context).launch();
            }
         });
         thread.start();
      }
   }
}