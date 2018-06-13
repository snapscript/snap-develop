package org.snapscript.studio.agent.task;

import static org.snapscript.studio.agent.event.ProcessEventType.WRITE_ERROR;
import static org.snapscript.studio.agent.event.ProcessEventType.WRITE_OUTPUT;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.snapscript.studio.agent.ConnectionListener;
import org.snapscript.studio.agent.ProcessEventStream;
import org.snapscript.studio.agent.event.ProcessEventChannel;

public class ConsoleConnector implements ConnectionListener{
   
   private final ProcessEventStream errorAdapter;
   private final ProcessEventStream outputAdapter;
   private final PrintStream originalOutput;
   private final PrintStream originalError;
   private final PrintStream output;
   private final PrintStream error;
   
   public ConsoleConnector(ProcessEventChannel channel, String process) throws Exception {
      this.errorAdapter = new ProcessEventStream(WRITE_ERROR, channel, System.err, process);
      this.outputAdapter = new ProcessEventStream(WRITE_OUTPUT, channel, System.out, process);
      this.output = new ConsoleStream(outputAdapter, System.out, true, "UTF-8");
      this.error = new ConsoleStream(errorAdapter, System.err, true, "UTF-8");
      this.originalError = System.err;
      this.originalOutput = System.out;
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
   
   @Override
   public void onClose() {
      try {
         System.setOut(originalOutput);
         System.setErr(originalError);
      }catch(Exception e) {
         System.err.println(ExceptionBuilder.build(e));
      }
   }
   
   private static class ConsoleStream extends PrintStream {
      
      private final PrintStream stream;
      
      public ConsoleStream(OutputStream out, PrintStream stream, boolean autoFlush, String encoding) throws UnsupportedEncodingException {
         super(out, autoFlush, encoding);
         this.stream = stream;
      }

      @Override
      public void close(){
         stream.close(); // do not allow android to close event stream
      } 
   }
}