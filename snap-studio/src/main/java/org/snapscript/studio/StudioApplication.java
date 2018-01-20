package org.snapscript.studio;

import java.util.Map;
import java.util.Set;

import javax.swing.UIManager;

import org.snapscript.studio.cli.ScriptService;
import org.snapscript.studio.project.ProjectMode;
import org.snapscript.studio.service.core.CommandLineArgument;
import org.snapscript.studio.service.core.CommandLineParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StudioApplication {

   private static final String ABOUT_NAME = "Snap Develop";
   private static final String PROJECT_MODE = "project-mode";
   private static final String MODE_ARGUMENT = "mode";
   private static final String DEFAULT_MODE = "develop";
   private static final String RUN_MODE = "run";
   
   public static void main(String[] list) throws Exception {
      Map<String, String> commands = CommandLineParser.parse(list);
      Set<String> names = commands.keySet();
      String mode = DEFAULT_MODE;
      
      //ThreadMonitor.start(5000);
      
      for(String name : names) {
         String value= commands.get(name);
         
         System.out.println("--" + name + "=" + value);
         System.setProperty(name, value); // make available to configuration
      }
      String server = CommandLineArgument.SERVER_ONLY.getValue();

      if(Boolean.parseBoolean(server)) {
         System.setProperty("java.awt.headless", "true");
      } else {
         System.setProperty("apple.laf.useScreenMenuBar", "true");
         System.setProperty("com.apple.mrj.application.apple.menu.about.name", ABOUT_NAME);
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      if(commands.containsKey(MODE_ARGUMENT)) { // is there a mode setting
         mode = commands.get(MODE_ARGUMENT);
      }
      if(!mode.equals(RUN_MODE)) {
         if(!mode.equals(DEFAULT_MODE)) {
            System.setProperty(PROJECT_MODE, ProjectMode.SINGLE_MODE);
         } else {
            System.setProperty(PROJECT_MODE, ProjectMode.DEVELOP_MODE);
         }
         SpringApplication.run(StudioApplication.class, list);
      } else {
         ScriptService.main(list);
      }
   }
}
