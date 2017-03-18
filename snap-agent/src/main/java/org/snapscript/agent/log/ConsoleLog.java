
package org.snapscript.agent.log;

import java.io.PrintStream;

public class ConsoleLog implements ProcessLog{
   
   private final PrintStream stream;
   
   public ConsoleLog() {
      this.stream = System.out;
   }

   @Override
   public void log(Object text) {
      stream.println(text);
   }

   @Override
   public void log(Object text, Throwable cause) {
      stream.print(text);
      
      if(cause != null) {
         stream.print(": ");
         cause.printStackTrace(stream);
      }else {
         stream.println();
      }
   }

}
