package org.snapscript.develop.browser;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class Browser extends Region {

   private final WebEngine webEngine;
   private final WebView browser;
   
   public Browser() {
      this.browser = new WebView();
      this.webEngine = browser.getEngine();
   }
   
   public void show(String target) {
      getStyleClass().add("browser");
      webEngine.load(target);
      getChildren().add(browser);
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
