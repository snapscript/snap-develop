package org.snapscript.agent.event;

import static org.snapscript.agent.event.ProcessEventType.START;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BeginEventMarshaller implements ProcessEventMarshaller<BeginEvent> {

   @Override
   public BeginEvent fromMessage(MessageEnvelope message) throws IOException {
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
      ByteArrayInputStream buffer = new ByteArrayInputStream(array, offset, length);
      DataInputStream input = new DataInputStream(buffer);
      String process = input.readUTF();
      String project = input.readUTF();
      String resource = input.readUTF();
      long duration = input.readLong();
      
      return new BeginEvent.Builder(process)
         .withProject(project)
         .withResource(resource)
         .withDuration(duration)
         .build();
   }

   @Override
   public MessageEnvelope toMessage(BeginEvent event) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(buffer);
      String process = event.getProcess();
      String project = event.getProject();
      String resource = event.getResource();
      long duration = event.getDuration();
      
      output.writeUTF(process);
      output.writeUTF(project);
      output.writeUTF(resource);
      output.writeLong(duration);
      output.flush();
      
      byte[] array = buffer.toByteArray();
      return new MessageEnvelope(process, START.code, array, 0, array.length);
   }
}
