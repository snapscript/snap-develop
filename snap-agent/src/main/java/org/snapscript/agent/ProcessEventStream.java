package org.snapscript.agent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.agent.event.ProcessEventType;
import org.snapscript.agent.event.WriteErrorEvent;
import org.snapscript.agent.event.WriteOutputEvent;

public class ProcessEventStream extends OutputStream {

   private final ByteArrayOutputStream buffer;
   private final ProcessEventChannel channel;
   private final ProcessEventType type;
   private final PrintStream stream;
   private final String process;
   
   public ProcessEventStream(ProcessEventType type, ProcessEventChannel channel, PrintStream stream, String process) {
      this.buffer = new ByteArrayOutputStream();
      this.process = process;
      this.channel = channel;
      this.stream = stream;
      this.type = type;
   }
   
   @Override
   public void write(int octet) throws IOException {
      write(new byte[]{(byte)octet});
   }
   
   @Override
   public void write(byte[] octets) throws IOException {
      write(octets, 0, octets.length);
   }
   
   @Override
   public void write(byte[] octets, int offset, int length) throws IOException {
      try {
         buffer.write(octets, offset, length);
         stream.write(octets, offset, length);
      }catch(Exception e) {
         throw new IOException("Error sending write event");
      }
   }
   
   @Override
   public void flush() throws IOException {
      try {
         byte[] octets = buffer.toByteArray();
         
         if(type == ProcessEventType.WRITE_ERROR) {
            WriteErrorEvent event = new WriteErrorEvent.Builder(process)
               .withData(octets)
               .withOffset(0)
               .withLength(octets.length)
               .build();
   
            channel.sendAsync(event);
            stream.flush();
         } else {
            WriteOutputEvent event = new WriteOutputEvent.Builder(process)
               .withData(octets)
               .withOffset(0)
               .withLength(octets.length)
               .build();
            
            channel.sendAsync(event);
            stream.flush();
         }
      }catch(Exception e) {
         throw new IOException("Error sending write event");
      } finally {
         buffer.reset();
      }
   }
   
}