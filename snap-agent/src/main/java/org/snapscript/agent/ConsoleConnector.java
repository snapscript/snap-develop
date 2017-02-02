package org.snapscript.agent;

import static org.snapscript.agent.event.ProcessEventType.WRITE_ERROR;
import static org.snapscript.agent.event.ProcessEventType.WRITE_OUTPUT;

import java.io.PrintStream;

import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.core.Console;
import org.snapscript.core.StreamConsole;

public class ConsoleConnector {
   
   private final ProcessEventStream errorAdapter;
   private final ProcessEventStream outputAdapter;
   private final PrintStream output;
   private final PrintStream error;
   private final Console console;
   
   public ConsoleConnector(ProcessEventChannel channel, String process) throws Exception {
      this.errorAdapter = new ProcessEventStream(WRITE_ERROR, channel, System.err, process);
      this.outputAdapter = new ProcessEventStream(WRITE_OUTPUT, channel, System.out, process);
      this.output = new PrintStream(outputAdapter, true, "UTF-8");
      this.error = new PrintStream(errorAdapter, true, "UTF-8");
      this.console = new StreamConsole(output);
   }

   public Console connect() {
      try {
         // redirect all output to the streams
         System.setOut(output);
         System.setErr(error);
      }catch(Exception e) {
         System.err.println(ExceptionBuilder.build(e));
      }
      return console;
   }
}
