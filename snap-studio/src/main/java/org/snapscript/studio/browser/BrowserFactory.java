package org.snapscript.studio.browser;

import org.snapscript.studio.configuration.OperatingSystem;

public class BrowserFactory {
   
   private static final String CEF_ENGINE = "cef";
   
   public static BrowserClient createBrowser(BrowserContext context) {
      OperatingSystem os = OperatingSystem.resolveSystem();
      String engine = context.getEngine();
      
      context.getLogger().info("Engine is " + engine);
      
      if(os.isWindows() && CEF_ENGINE.equals(engine)) {
         return new ChromiumBrowserClient(context);
      }
      return new JavaFXBrowserClient(context);
   }
}
