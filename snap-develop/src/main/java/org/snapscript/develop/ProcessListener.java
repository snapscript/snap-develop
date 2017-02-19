/*
 * ProcessListener.java December 2016
 *
 * Copyright (C) 2016, Niall Gallagher <niallg@users.sf.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

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
