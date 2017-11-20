package org.snapscript.studio.browser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.snapscript.studio.common.ClassPathReader;

public class JavaFXApplication extends Application {
   
   private static BrowserContext context;
   private JavaFXBrowser browser;
   private Scene scene;

   @Override
   public void start(Stage stage) {
      try {
         MenuBar menuBar = new MenuBar();
         Menu menu = new Menu("File");
         MenuItem quit = new MenuItem("Quit");
         menuBar.setUseSystemMenuBar(true);
         menuBar.useSystemMenuBarProperty().set(true);
         quit.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
               Platform.exit();
            }
         });
         menu.getItems().add(quit);
         menuBar.getMenus().add(menu);
         stage.setTitle(context.getDirectory().getCanonicalPath());
         
         if(isPlatformWindows()) {
            byte[] data = ClassPathReader.findResource(context.getIconPath());
            InputStream stream = new ByteArrayInputStream(data);
            Image image = new Image(stream);
         
            stage.getIcons().add(image);
         }
      }catch(Exception e) {
         context.getLogger().info("Could not load image", e);
      }
      browser = new JavaFXBrowser(context);
      scene = new Scene(browser, 1200, 800, Color.web("#666970"));
      stage.setScene(scene);
      stage.show();
      browser.show(context.getTarget());
   }
   
   private static boolean isPlatformWindows() {
      String platform = System.getProperty("os.name");
      return platform != null && platform.contains("indows");
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