package org.snapscript.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.snapscript.core.FilePathConverter;
import org.snapscript.core.MapModel;
import org.snapscript.core.Model;
import org.snapscript.core.Path;
import org.snapscript.core.PathConverter;

public class CommandLineParser {
   
   private static final String DIRECTORY = "root";
   private static final String SCRIPT = "script";
   private static final String EVALUATE = "evaluate";
   private static final String CLASSPATH = "classpath";

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
      String root = ".";
      Path script = null;
      String evaluate = null;
      
      for(String option : options) {
         if(!option.startsWith("--")) {
            throw new IllegalArgumentException("Illegal option '" + option + "'");
         }
         String command = option.substring(2);
         String[] pair = command.split("=");
         
         if(pair.length > 1) {
            String name = pair[0];
            String value = pair[1];
            
            if(name.equals(DIRECTORY)) {
               root = value;
            } else if(name.equals(CLASSPATH)) {
               classpath = new File(value);
            } else if(name.equals(SCRIPT)) {
               script = converter.createPath(value);
            } else if(name.equals(EVALUATE)) {
               evaluate = value;
            } else {
               values.put(name, value);
            }
         }
      }
      return new CommandLine(model, root, classpath, script, evaluate);
   }
}