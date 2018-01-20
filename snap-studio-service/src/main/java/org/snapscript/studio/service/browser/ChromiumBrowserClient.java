package org.snapscript.studio.service.browser;

public class ChromiumBrowserClient implements BrowserClient {
   
   private final BrowserContext context;
   
   public ChromiumBrowserClient(BrowserContext context) {
      this.context = context;
   }
   
   public void launch() {
      ChromiumApplication.launch(context);
   }
}
