package org.snapscript.agent.event;

import static org.snapscript.agent.event.ProcessEventType.EXIT;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ExitEventMarshaller implements ProcessEventMarshaller<ExitEvent> {

   @Override
   public ExitEvent fromMessage(MessageEnvelope message) throws IOException {
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
      ByteArrayInputStream buffer = new ByteArrayInputStream(array, offset, length);
      DataInputStream input = new DataInputStream(buffer);
      String process = input.readUTF();
      long duration = input.readLong();
      
      return new ExitEvent.Builder(process)
         .withDuration(duration)
         .build();
      
   }

   @Override
   public MessageEnvelope toMessage(ExitEvent value) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(buffer);
      String process = value.getProcess();
      long duration = value.getDuration();
      
      output.writeUTF(process);
      output.writeLong(duration);
      output.flush();
      
      byte[] array = buffer.toByteArray();
      return new MessageEnvelope(process, EXIT.code, array, 0, array.length);
   }
}
