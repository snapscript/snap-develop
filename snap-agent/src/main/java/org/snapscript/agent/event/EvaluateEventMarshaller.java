package org.snapscript.agent.event;

import static org.snapscript.agent.event.ProcessEventType.EVALUATE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EvaluateEventMarshaller implements ProcessEventMarshaller<EvaluateEvent> {

   @Override
   public EvaluateEvent fromMessage(MessageEnvelope message) throws IOException {
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
      ByteArrayInputStream buffer = new ByteArrayInputStream(array, offset, length);
      DataInputStream input = new DataInputStream(buffer);
      String process = input.readUTF();
      String expression = input.readUTF();

      return new EvaluateEvent.Builder(process)
         .withExpression(expression)
         .build();
   }

   @Override
   public MessageEnvelope toMessage(EvaluateEvent event) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(buffer);
      String expression = event.getExpression();
      String process = event.getProcess();
      
      output.writeUTF(process);
      output.writeUTF(expression);
      output.flush();
      
      byte[] array = buffer.toByteArray();
      return new MessageEnvelope(process, EVALUATE.code, array, 0, array.length);
   }
}
