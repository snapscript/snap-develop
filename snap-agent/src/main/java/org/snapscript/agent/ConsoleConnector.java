package org.snapscript.agent;

import static org.snapscript.agent.event.ProcessEventType.WRITE_ERROR;
import static org.snapscript.agent.event.ProcessEventType.WRITE_OUTPUT;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.snapscript.agent.event.ProcessEventChannel;

public class ConsoleConnector {
   
   private final ProcessEventStream errorAdapter;
   private final ProcessEventStream outputAdapter;
   private final PrintStream output;
   private final PrintStream error;
   
   public ConsoleConnector(ProcessEventChannel channel, String process) throws Exception {
      this.errorAdapter = new ProcessEventStream(WRITE_ERROR, channel, System.err, process);
      this.outputAdapter = new ProcessEventStream(WRITE_OUTPUT, channel, System.out, process);
      this.output = new ConsoleStream(outputAdapter, true, "UTF-8");
      this.error = new ConsoleStream(errorAdapter, true, "UTF-8");
   }

   public void connect() {
      try {
         // redirect all output to the streams
         System.setOut(output);
         System.setErr(error);
      }catch(Exception e) {
         System.err.println(ExceptionBuilder.build(e));
      }
   }
   
   private static class ConsoleStream extends PrintStream {
      
      public ConsoleStream(OutputStream out, boolean autoFlush, String encoding) throws UnsupportedEncodingException {
         super(out, autoFlush, encoding);
      }

      @Override
      public void close(){} // do not allow android to close
   }
}
