package org.snapscript.agent;

import static org.snapscript.agent.event.ProcessEventType.WRITE_ERROR;
import static org.snapscript.agent.event.ProcessEventType.WRITE_OUTPUT;

import java.io.PrintStream;

import org.snapscript.agent.event.ProcessEventChannel;

public class ConsoleConnector {

   public static void connect(ProcessEventChannel channel, String process) {
      try {
         ProcessEventStream errorAdapter = new ProcessEventStream(WRITE_ERROR, channel, System.err, process);
         ProcessEventStream outputAdapter = new ProcessEventStream(WRITE_OUTPUT, channel, System.out, process);
         PrintStream outputStream = new PrintStream(outputAdapter, false, "UTF-8");
         PrintStream errorStream = new PrintStream(errorAdapter, false, "UTF-8");
         
         // redirect all output to the streams
         System.setOut(outputStream);
         System.setErr(errorStream);
      }catch(Exception e) {
         System.err.println(ExceptionBuilder.build(e));
      }
   }
}
