package org.snapscript.studio.agent.cli;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.snapscript.core.module.FilePathConverter;
import org.snapscript.core.module.Path;
import org.snapscript.core.module.PathConverter;

public class CommandOptionParser {
   
   private static final String ILLEGAL_OPTION = "Illegal option '%s', options take the format --<option>=<value>";
   private static final String UNKNOWN_OPTION = "Unknown option '%s', options take the format --<option>=<value>";
   private static final String INVALID_VALUE = "Invalid value '%s' for '%s' should match pattern '%s'";
   
   private final List<? extends CommandOption> options;
   private final PathConverter converter;
   
   public CommandOptionParser(List<? extends CommandOption> options) {
      this.converter = new FilePathConverter();
      this.options = options;
   }
   
   public CommandValue parse(String argument) {
      if(!argument.startsWith("--")) {
         String warning = String.format(ILLEGAL_OPTION, argument);
         CommandLineUsage.usage(options, warning);
      }
      String command = argument.substring(2);
      String[] pair = command.split("=");
      String key = pair[0];
      CommandOption option = resolve(key);
      
      if(option == null) {
         String warning = String.format(UNKNOWN_OPTION, key);
         CommandLineUsage.usage(options, warning);
      }
      Class type = option.getType();
      String name = option.getName();
      String value = pair[0];
      
      if(pair.length > 1) {
         value = pair[1];
      } else if(option != null){
         value = option.getDefault();
      } 
      if(value == null) {
         String warning = String.format(ILLEGAL_OPTION, option);
         CommandLineUsage.usage(options, warning);
      }
      int length = value.length();
      
      if(length > 1) {
         String start = value.substring(0, 1);
         
         if(start.equals("\"") || start.equals("\'")) {
            if(value.endsWith(start)) {
               value = value.substring(1, length - 1);
            }
         }
      }
      Pattern pattern = option.getPattern();
      Matcher matcher = pattern.matcher(value);
      
      if(!matcher.matches()) {
         String warning = String.format(INVALID_VALUE, value, name, pattern);
         CommandLineUsage.usage(options, warning);
      }
      Object object = convert(value, type);
      return new CommandValue(name, object);
   } 
   
   public Object convert(String value, Class type) {
      try {
         if(type == File[].class) {
            StringTokenizer tokenizer = new StringTokenizer(value, File.pathSeparator);
            List<File> files = new ArrayList<File>();
            
            while(tokenizer.hasMoreTokens()) {
               String token = tokenizer.nextToken();
               int length = token.length();
               
               if(length > 0) {
                  File file = new File(token);
                  files.add(file);
               }
            }
            return Collections.unmodifiableList(files);
         }
         if(type == Boolean.class) {
            return Boolean.parseBoolean(value);
         }
         if(type == Integer.class) {
            return Integer.parseInt(value);
         }
         if(type == URI.class) {
            if(!value.startsWith("http:") && !value.startsWith("https:")) {
               throw new IllegalStateException("Resource '" + value + "' is not a url");
            }
            return new URI(value);
         }
         if(type == File.class) {
            return new File(value);
         }
         if(type == Path.class) {
            return converter.createPath(value);
         }
         if(type.isEnum()) {
            return Enum.valueOf(type, value);
         }
         return value;
      } catch(Exception e) {
         throw new IllegalStateException("Error parsing " + value, e);
      }
   }
   
   private CommandOption resolve(String prefix) {
      for(CommandOption option : options) {
         String name = option.getName();
         String code = option.getCode();
         
         if(name.equalsIgnoreCase(prefix)) {
            return option;
         }
         if(code.equalsIgnoreCase(prefix)) {
            return option;
         }
      }
      return null;
   }
}
