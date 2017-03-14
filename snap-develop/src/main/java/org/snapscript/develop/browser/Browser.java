package org.snapscript.develop.browser;

import java.lang.reflect.Method;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class Browser extends Region {

   private final BrowserContext context;
   private final WebEngine engine;
   private final WebView browser;

   public Browser(BrowserContext context) {
      this.browser = new WebView();
      this.engine = browser.getEngine();
      this.context = context;
   }

   public void show(String target) {
      engine.getLoadWorker().stateProperty().addListener(new ChangeListener() {

         @Override
         public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            try {
               Object window = engine.executeScript("window");
               Class type = window.getClass();
               Method method = type.getDeclaredMethod("setMember", String.class, Object.class);

               method.setAccessible(true);
               method.invoke(window, "java", Browser.this);
               engine.executeScript("console.log = function(message){java.log(message);};\n");
            } catch (Exception e) {
               context.getLogger().info("Could not register log listener", e);
            }
         }

      });
      engine.documentProperty().addListener(new ChangeListener() {
         @Override
         public void changed(ObservableValue prop, Object oldDoc, Object newDoc) {
            enableFirebug(engine);
         }
      });
      getStyleClass().add("browser");
      engine.load(target);
      getChildren().add(browser);
   }

   private void enableFirebug(WebEngine engine) {
      if(context.isDebug()) {
         engine.executeScript("if (!document.getElementById('FirebugLite')){E = document['createElement' + 'NS'] && document.documentElement.namespaceURI;E = E ? document['createElement' + 'NS'](E, 'script') : document['createElement']('script');E['setAttribute']('id', 'FirebugLite');E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');E['setAttribute']('FirebugLite', '4');(document['getElementsByTagName']('head')[0] || document['getElementsByTagName']('body')[0]).appendChild(E);E = new Image;E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');}");
      }
   }

   public void log(Object message) {
      context.getLogger().info(String.valueOf(message));
   }

   @Override
   protected void layoutChildren() {
      double width = getWidth();
      double height = getHeight();
      layoutInArea(browser, 0, 0, width, height, 0, HPos.CENTER, VPos.CENTER);
   }

   @Override
   protected double computePrefWidth(double height) {
      return 750;
   }

   @Override
   protected double computePrefHeight(double width) {
      return 500;
   }
}
