package org.snapscript.agent;

import org.snapscript.core.Console;

public class ProcessConsole implements Console {
   
   private volatile Console console;
   
   public ProcessConsole() {
      this(null);
   }
   
   public ProcessConsole(Console console) {
      this.console = console;
   }
   
   public void update(Console console) {
      this.console = console;
   }

   @Override
   public void print(Object value) {
      if(console != null) {
         console.print(value);
      }
   }

   @Override
   public void println(Object value) {
      if(console != null) {
         console.println(value);
      }
   }

   @Override
   public void println() {
      if(console != null) {
         console.println();
      } 
   }
}
