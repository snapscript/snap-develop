package org.snapscript.develop.browser;

import java.io.File;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class BrowserApplication extends Application {
   
   private static String target;
   private static String title;
   
   private Browser browser;
   private Scene scene;

   @Override
   public void start(Stage stage) {
      stage.setTitle(title);
      browser = new Browser();
      scene = new Scene(browser, 800, 800, Color.web("#666970"));
      stage.setScene(scene);
      stage.show();
      browser.show(target);
   }

   public static void launch(File directory, String host, int port) {
      try {
         title = directory.getCanonicalPath();
         target = String.format("http://%s:%s", host, port);
         launch(new String[]{});
      }catch(Exception e) {
         throw new IllegalStateException("Could not create browser", e);
      }
   }
}