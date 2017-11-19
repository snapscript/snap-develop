package org.snapscript.studio.core;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandLineParser {

   public static Map<String, String> parse(String[] list) throws Exception {
      Map<String, String> commands = new HashMap<String, String>();
      
      if(list != null) {
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