package org.snapscript.studio.service.browser;

import lombok.extern.slf4j.Slf4j;

import org.snapscript.studio.project.config.OperatingSystem;

@Slf4j
public class BrowserFactory {
   
   public static BrowserClient createBrowser(BrowserContext context) {
      OperatingSystem os = OperatingSystem.resolveSystem();
      BrowserEngine engine = context.getEngine();
      
      log.info("Engine is " + engine);
      
      if(os.isWindows() && engine.isChromium()) {
         return new ChromiumBrowserClient(context);
      }
      return new JavaFXBrowserClient(context);
   }
}
