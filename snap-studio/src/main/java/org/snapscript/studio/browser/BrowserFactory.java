package org.snapscript.studio.browser;

import lombok.extern.slf4j.Slf4j;

import org.snapscript.studio.project.config.OperatingSystem;

@Slf4j
public class BrowserFactory {
   
   private static final String CEF_ENGINE = "cef";
   
   public static BrowserClient createBrowser(BrowserContext context) {
      OperatingSystem os = OperatingSystem.resolveSystem();
      String engine = context.getEngine();
      
      log.info("Engine is " + engine);
      
      if(os.isWindows() && CEF_ENGINE.equals(engine)) {
         return new ChromiumBrowserClient(context);
      }
      return new JavaFXBrowserClient(context);
   }
}
