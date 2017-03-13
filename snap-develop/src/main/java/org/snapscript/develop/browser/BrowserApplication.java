package org.snapscript.develop.browser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.develop.http.loader.ClassResourceLoader;

public class BrowserApplication extends Application {
   
   private static final String ICON_PATH = "/resource/img/icon.png";
   
   private static ProcessLogger log;
   private static String target;
   private static String title;
   
   private Browser browser;
   private Scene scene;

   @Override
   public void start(Stage stage) {
      stage.setTitle(title);
      try {
         byte[] data = ClassResourceLoader.loadResource(ICON_PATH);
         InputStream stream = new ByteArrayInputStream(data);
         Image image = new Image(stream);
         stage.getIcons().add(image); 
      }catch(Exception e) {
         log.info("Could not load image", e);
      }
      browser = new Browser(log);
      scene = new Scene(browser, 800, 800, Color.web("#666970"));
      stage.setScene(scene);
      stage.show();
      browser.show(target);
   }
   
   public static void launch(ProcessLogger logger, File directory, String host, int port) {
      try {
         log = logger;
         title = directory.getCanonicalPath();
         target = String.format("http://%s:%s", host, port);
         launch(new String[]{});
      }catch(Exception e) {
         throw new IllegalStateException("Could not create browser", e);
      } finally {
         System.exit(0);
      }
   }
}