
package org.snapscript.agent;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionBuilder {

   public static String build(Exception cause) {
      StringWriter buffer = new StringWriter();
      PrintWriter writer = new PrintWriter(buffer);
      
      cause.printStackTrace(writer);
      writer.flush();
      writer.close();
      
      return buffer.toString();
   }
}
