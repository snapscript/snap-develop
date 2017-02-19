/*
 * ConsoleConnector.java December 2016
 *
 * Copyright (C) 2016, Niall Gallagher <niallg@users.sf.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

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
      this.output = new ConsoleStream(outputAdapter, System.out, true, "UTF-8");
      this.error = new ConsoleStream(errorAdapter, System.err, true, "UTF-8");
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
