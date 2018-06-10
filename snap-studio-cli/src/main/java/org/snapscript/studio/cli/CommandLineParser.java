package org.snapscript.studio.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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
      List<File> classpath = new ArrayList<File>();
      File directory = new File(".");
      String url = null;
      Path script = null;
      String evaluate = null;
      Integer port = null;
      boolean debug = Boolean.parseBoolean(CommandLineArgument.VERBOSE.value);
      boolean check = Boolean.parseBoolean(CommandLineArgument.CHECK.value);
      
      classpath.add(directory);
      
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
         } 
         if(value == null) {
            String warning = String.format(TEMPLATE, option);
            CommandLineUsage.usage(warning);
         }
         if(argument != null) {
            if(argument.isVerbose()) {
               debug = Boolean.parseBoolean(value);
            } else if(argument.isCheck()) {
               check = Boolean.parseBoolean(value);
            } else if(argument.isPort()) {
               port = Integer.parseInt(value);               
            } else if(argument.isURL()) {
               url = value;
            } else if(argument.isDirectory()) {
               directory = new File(value);
            } else if(argument.isClassPath()) {
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
               classpath = Collections.unmodifiableList(files);
            } else if(argument.isScript()) {
               script = converter.createPath(value);
            } else if(argument.isExpression()) {
               evaluate = value;
            }
         } else {
            values.put(name, value);
         }
      }
      return new CommandLine(model, url, directory, classpath, script, evaluate, port, debug, check);
   }
}