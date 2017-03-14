package org.snapscript.develop.browser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.snapscript.develop.http.loader.ClassResourceLoader;

public class BrowserApplication extends Application {
   
   private static BrowserContext context;
   private Browser browser;
   private Scene scene;

   @Override
   public void start(Stage stage) {
      try {
         stage.setTitle(context.getDirectory().getCanonicalPath());
         byte[] data = ClassResourceLoader.loadResource(context.getIconPath());
         InputStream stream = new ByteArrayInputStream(data);
         Image image = new Image(stream);
         stage.getIcons().add(image); 
      }catch(Exception e) {
         context.getLogger().info("Could not load image", e);
      }
      browser = new Browser(context);
      scene = new Scene(browser, 800, 800, Color.web("#666970"));
      stage.setScene(scene);
      stage.show();
      browser.show(context.getTarget());
   }
   
   public static void launch(BrowserContext value) {
      try {
         context = value;
         launch(new String[]{});
      }catch(Exception e) {
         throw new IllegalStateException("Could not create browser", e);
      } finally {
         System.exit(0);
      }
   }
}