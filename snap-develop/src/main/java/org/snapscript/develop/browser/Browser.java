package org.snapscript.develop.browser;

import java.lang.reflect.Method;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import org.snapscript.agent.log.ProcessLogger;

public class Browser extends Region {

   private final ProcessLogger logger;
   private final WebEngine engine;
   private final WebView browser;
   
   public Browser(ProcessLogger logger) {
      this.browser = new WebView();
      this.engine = browser.getEngine();
      this.logger = logger;
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
            }catch(Exception e) {
               logger.info("Could not register log listener", e);
            }
         }
         
      });
      getStyleClass().add("browser");
      engine.load(target);
      getChildren().add(browser);
   }

   public void log(Object message) {
      logger.info(String.valueOf(message));
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
