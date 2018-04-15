package org.snapscript.studio.cli;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CommandLineUsage {

   public static void usage() {
      usage(null);
   }
   
   public static void usage(String warning) {
      StringWriter builder = new StringWriter();
      PrintWriter writer = new PrintWriter(builder);
      
      if(warning != null) {
         writer.println(warning);
         writer.println();
      }
      writer.println("Usage:");
      writer.println();
      
      CommandLineArgument[] arguments = CommandLineArgument.values();
      
      for(CommandLineArgument argument : arguments) {
         writer.print("--");
         writer.print(argument.code);
         
         for(int i = argument.code.length(); i < 4; i++){
            writer.print(" ");
         }
         writer.print("--");
         writer.print(argument.name);
         
         for(int i = argument.name.length(); i < 15; i++){
            writer.print(" ");
         }
         writer.print(argument.description);
         writer.println();
      }
      writer.println();
      writer.flush();
      writer.close();
      System.err.println(builder);
      System.err.flush();
      System.exit(0);
   }
}
