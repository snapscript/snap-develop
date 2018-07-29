package org.snapscript.studio.service.browser;

public enum BrowserEngine {
   CEF("cef"),
   JAVAFX("javafx");
   
   public final String name;
   
   private BrowserEngine(String name) {
      this.name = name;
   }
   
   public boolean isChromium() {
      return this == CEF;
   }
   
   public boolean isJavaFX() {
      return this == JAVAFX;
   }
   
   public static BrowserEngine resolveEngine(String token) {
      if(token != null) {
         BrowserEngine[] engines = BrowserEngine.values();
         
         for(BrowserEngine engine : engines) {
            if(engine.name.equalsIgnoreCase(token)) {
               return engine;
            }
         }
      }
      return JAVAFX;
   }
}
