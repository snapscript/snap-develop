package org.snapscript.studio;

import java.util.Map;
import java.util.Set;

import javax.swing.UIManager;

import org.snapscript.service.ScriptService;
import org.snapscript.studio.common.ThreadMonitor;
import org.snapscript.studio.resource.project.ProjectMode;

//--mode=develop --directory=work --port=4457 --agent-pool=4 --agent-port=4456 --log-level=DEBUG
public class ApplicationLauncher {

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
         ApplicationContext service = new ApplicationContext("/context/" + mode + ".xml");
         
         if(!mode.equals(DEFAULT_MODE)) {
            System.setProperty(PROJECT_MODE, ProjectMode.SINGLE_MODE);
         } else {
            System.setProperty(PROJECT_MODE, ProjectMode.MULTIPLE_MODE);
         }
         service.start();
      } else {
         ScriptService.main(list);
      }
   }
}