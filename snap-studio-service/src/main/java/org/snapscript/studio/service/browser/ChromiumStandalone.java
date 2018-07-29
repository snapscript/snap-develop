package org.snapscript.studio.service.browser;

import java.io.File;
import java.net.URI;

public class ChromiumStandalone {

   public static void main(String[] list) {
      if(list == null) {
         throw new IllegalArgumentException("URL destination required");
      }
      BrowserContext context = new BrowserContext.Builder()
         .withDebug(true)
         .withDirectory(new File("."))
         .withEngine(BrowserEngine.CEF)
         .withHost(URI.create(list[0]).getHost())
         .withPort(URI.create(list[0]).getPort())
         .build();
      
      ChromiumApplication.launch(context);
   }
}
