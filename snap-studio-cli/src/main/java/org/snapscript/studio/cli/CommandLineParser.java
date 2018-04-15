package org.snapscript.studio.cli;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.snapscript.core.module.FilePathConverter;
import org.snapscript.core.module.Path;
import org.snapscript.core.module.PathConverter;
import org.snapscript.core.scope.MapModel;
import org.snapscript.core.scope.Model;

public class CommandLineParser {
   
   private static final String TEMPLATE = "Illegal option '%s', options take the format --<option>=<value>";

   private final Map<String, Object> values;
   private final PathConverter converter;
   private final Model model;
   
   public CommandLineParser() {
      this.values = new HashMap<String, Object>();
      this.converter = new FilePathConverter();
      this.model = new MapModel(values);
   }
   
   public CommandLine parse(String[] options) throws Exception {
      File classpath = new File(".");
      File directory = new File(".");
      String url = null;
      Path script = null;
      String evaluate = null;
      boolean debug = false;
      
      for(String option : options) {
         if(!option.startsWith("--")) {
            String warning = String.format(TEMPLATE, option);
            CommandLineUsage.usage(warning);
         }
         String command = option.substring(2);
         String[] pair = command.split("=");
         String name = pair[0];
         CommandLineArgument argument = CommandLineArgument.resolveArgument(name);
         String value = null;
         
         if(pair.length > 1) {
            value = pair[1];
         } else if(argument != null){
            value = argument.value;
         } else {
            values.put(name, value);
         }
         if(value != null) {
            if(argument.isVerbose()) {
               debug = Boolean.parseBoolean(value);
            } else if(argument.isURL()) {
               url = value;
            } else if(argument.isDirectory()) {
               directory = new File(value);
            } else if(argument.isClassPath()) {
               classpath = new File(value);
            } else if(argument.isScript()) {
               script = converter.createPath(value);
            } else if(argument.isExpression()) {
               evaluate = value;
            }
         } else {
            String warning = String.format(TEMPLATE, option);
            CommandLineUsage.usage(warning);
         }
      }
      return new CommandLine(model, url, directory, classpath, script, evaluate, debug);
   }
}