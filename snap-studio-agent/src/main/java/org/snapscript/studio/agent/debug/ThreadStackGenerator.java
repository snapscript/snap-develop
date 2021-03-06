package org.snapscript.studio.agent.debug;

import org.snapscript.core.stack.ThreadStack;

public class ThreadStackGenerator {

   private final ThreadStack stack;
   
   public ThreadStackGenerator(ThreadStack stack) {
      this.stack = stack;
   }
   
   public String generate() {
      StackTraceElement[] elements = stack.build();
      
      if(elements.length > 0) {
         StringBuilder builder = new StringBuilder();
         
         for(StackTraceElement element : elements){
            builder.append(element);
            builder.append("\n");
         }
         return builder.toString();
      }
      return "";
   }
}