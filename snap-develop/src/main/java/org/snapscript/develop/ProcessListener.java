package org.snapscript.develop;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.snapscript.agent.log.ProcessLogger;

public class ProcessListener implements ConsoleListener {
   
   private final ProcessLogger logger;
   private final Pattern pattern;
   
   public ProcessListener(ProcessLogger logger) {
      this.pattern = Pattern.compile("^([ |\\t]+).*", Pattern.DOTALL);
      this.logger = logger;
   }

   @Override
   public void onUpdate(String process, String text) {
      try {
         Matcher matcher = pattern.matcher(text);
         String trim = text.trim();
         
         if(matcher.matches()) {
            String indent = matcher.group(1);
            
            logger.info(process + ": " + indent + trim);
         } else {
            logger.info(process + ": " + trim);
         }
      }catch(Exception e) {
         e.printStackTrace();
      }
   }
   
   @Override
   public void onUpdate(String process, String text, Throwable cause) {
      try {
         Matcher matcher = pattern.matcher(text);
         String trim = text.trim();
         
         if(matcher.matches()) {
            String indent = matcher.group(1);
            
            logger.info(process + ": " + indent + trim, cause);
         } else {
            logger.info(process + ": " + trim, cause);
         }
      }catch(Exception e) {
         e.printStackTrace();
      }
   }
}