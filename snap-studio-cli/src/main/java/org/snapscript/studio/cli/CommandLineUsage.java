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
      int name = 0;
      int code = 0;
      int pad = 3;
      
      for(CommandLineArgument argument : arguments) {
         if(argument.code.length() > code) {
            code = argument.code.length();
         }
         if(argument.name.length() > name) {
            name = argument.name.length();
         }
      }
      for(CommandLineArgument argument : arguments) {
         writer.print("--");
         writer.print(argument.code);
         
         for(int i = argument.code.length(); i < code + pad; i++){
            writer.print(" ");
         }
         writer.print("--");
         writer.print(argument.name);
         
         for(int i = argument.name.length(); i < name + pad; i++){
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
