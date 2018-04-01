package org.snapscript.studio.cli;

import java.io.File;

public class ScriptService {

   public static void main(String[] options) throws Exception {
      CommandLineParser parser = new CommandLineParser();
      CommandLine line = parser.parse(options);
      File classpath = line.getClasspath();
      ScriptExecutor executor = new ScriptExecutor(line);
      
      ScriptClassLoader.update(classpath);
      line.validate();
      executor.execute();
   }
}