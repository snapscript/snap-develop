package org.snapscript.studio;

import static org.snapscript.studio.agent.runtime.RuntimeAttribute.VERSION;

import java.util.Map;
import java.util.Set;

import javax.swing.UIManager;

import org.snapscript.studio.agent.cli.CommandLine;
import org.snapscript.studio.agent.cli.CommandLineBuilder;
import org.snapscript.studio.agent.runtime.RuntimeAttribute;
import org.snapscript.studio.service.SplashScreen;
import org.snapscript.studio.service.StudioCommandLine;
import org.snapscript.studio.service.StudioOption;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StudioApplication {

   private static final String ABOUT_NAME = "Snap Develop";
   private static final String[] SEARCH_FILES = {
      "snapd.ini",
      "snap-studio.ini"
   };

   public static void main(String[] list) throws Exception {
      CommandLineBuilder builder = StudioOption.getBuilder();
      CommandLine local = builder.build(list, SEARCH_FILES);
      StudioCommandLine line = new StudioCommandLine(local);
      Map<String, Object> commands = local.getValues();
      Set<String> names = commands.keySet();
      String version = VERSION.getValue();

      //ThreadMonitor.start(5000);
      
      for(String name : names) {
         Object value = commands.get(name);
         String token = String.valueOf(value);
         
         System.out.println("--" + name + "=" + token);
         System.setProperty(name, token); // make available to configuration
      }
      if(line.isServerOnly()) {
         System.setProperty("java.awt.headless", "true");
      } else {
         System.setProperty("apple.laf.useScreenMenuBar", "true");
         System.setProperty("com.apple.mrj.application.apple.menu.about.name", ABOUT_NAME);
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         SplashScreen.getPanel().show(60000); // 1 minute
         SplashScreen.getPanel().update("Snap Studio " + version);
      }
      SpringApplication.run(StudioApplication.class, list);
      
   }
}
