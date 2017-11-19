package org.snapscript.studio.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

public class ProcessListener implements ConsoleListener {
   
   private final Pattern pattern;
   private final Logger logger;
   
   public ProcessListener(Logger logger) {
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