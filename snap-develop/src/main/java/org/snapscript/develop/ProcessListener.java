package org.snapscript.develop;

import org.snapscript.agent.ConsoleLogger;

public class ProcessListener implements ConsoleListener {
   
   private final ConsoleLogger logger;
   
   public ProcessListener(ConsoleLogger logger) {
      this.logger = logger;
   }

   @Override
   public void onUpdate(String process, String text) {
      try {
         String line = text.trim();
         logger.log(process + ": " + line);
      }catch(Exception e) {
         e.printStackTrace();
      }
   }
   
   @Override
   public void onUpdate(String process, String text, Throwable cause) {
      try {
         String line = text.trim();
         logger.log(process + ": " + line, cause);
      }catch(Exception e) {
         e.printStackTrace();
      }
   }
}
