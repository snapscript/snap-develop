/*
 * CommandLineParser.java December 2016
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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandLineParser {

   public static Map<String, String> parse(String[] list) throws Exception {
      Map<String, String> commands = new HashMap<String, String>();
      
      if(list.length > 0) {
         CommandLineArgument[] arguments = CommandLineArgument.values();
         
         for(CommandLineArgument argument : arguments) {
            String name = argument.command;
            String value = argument.value;
            
            if(value != null) {
               commands.put(name, value); // set defaults
            }
         }
         for(String argument : list) {
            if(!argument.startsWith("--")) {
               throw new IllegalArgumentException("Argument " + argument + " is illegal");
            }
            if(!argument.contains("=")) {
               throw new IllegalArgumentException("Argument " + argument + " has not value");
            }
            String token = argument.substring(2);
            String[] pair = token.split("=");
            String name = pair[0].trim();
            String value = pair[1].trim();
            int length = value.length();
            
            if(length > 1) {
               String start = value.substring(0, 1);
               
               if(start.equals("\"") || start.equals("\'")) {
                  if(value.endsWith(start)) {
                     value = value.substring(1, length - 1);
                  }
               }
            }
            if(value != null) {
               Pattern pattern = CommandLineArgument.getPattern(name);
               
               if(pattern != null) {
                  Matcher matcher = pattern.matcher(value);
                  
                  if(!matcher.matches()) {
                     System.out.println("--"+name+"="+value+ " does not match pattern "+pattern);
                  }
                  commands.put(name, value);
               }
            }
         }
      }
      return commands;
   }
}
