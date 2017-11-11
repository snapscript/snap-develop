package org.snapscript.studio.browser;

public class JavaFXBrowserClient implements BrowserClient {
   
   private final BrowserContext context;
   
   public JavaFXBrowserClient(BrowserContext context) {
      this.context = context;
   }

   @Override
   public void launch() {
      JavaFXApplication.launch(context);
   }

}
