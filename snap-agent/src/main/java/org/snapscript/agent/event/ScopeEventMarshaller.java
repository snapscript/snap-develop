package org.snapscript.agent.event;

import static org.snapscript.agent.event.ProcessEventType.SCOPE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ScopeEventMarshaller implements ProcessEventMarshaller<ScopeEvent> {

   @Override
   public ScopeEvent fromMessage(MessageEnvelope message) throws IOException {
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
      Map<String, Map<String, String>> variables = new TreeMap<String, Map<String, String>>();
      ByteArrayInputStream buffer = new ByteArrayInputStream(array, offset, length);
      DataInputStream input = new DataInputStream(buffer);
      String process = input.readUTF();
      String thread = input.readUTF();
      String stack = input.readUTF();
      String instruction = input.readUTF();
      String status = input.readUTF();
      String resource = input.readUTF();
      int line = input.readInt();
      int depth = input.readInt();
      int sequence = input.readInt();
      int count = input.readInt();
      
      for(int i = 0; i < count; i++) {
         Map<String, String> criteria = new HashMap<String, String>();
         String name = input.readUTF();
         int size = input.readInt();
         
         for(int j = 0; j < size; j++) {
            String key = input.readUTF();
            String value = input.readUTF();
            
            criteria.put(key, value);
         }
         variables.put(name, criteria);
      }
      return new ScopeEvent(process, thread, stack, instruction, status, resource, line, depth, sequence, variables);
   }

   @Override
   public MessageEnvelope toMessage(ScopeEvent event) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(buffer);
      Map<String, Map<String, String>> variables = event.getVariables();
      Set<String> names = variables.keySet();
      String process = event.getProcess();
      String thread = event.getThread();
      String stack = event.getStack();
      String instruction = event.getInstruction();
      String status = event.getStatus();
      String resource = event.getResource();
      int sequence = event.getKey();
      int line = event.getLine();
      int depth = event.getDepth();
      int count = variables.size();
      
      output.writeUTF(process);
      output.writeUTF(thread);
      output.writeUTF(stack);
      output.writeUTF(instruction);
      output.writeUTF(status);
      output.writeUTF(resource);
      output.writeInt(line);
      output.writeInt(depth);
      output.writeInt(sequence);
      output.writeInt(count);
      
      for(String name : names) {
         Map<String, String> criteria = variables.get(name);
         Set<String> keys = criteria.keySet();
         int size = criteria.size();
         
         output.writeUTF(name);
         output.writeInt(size);
         
         for(String key : keys) {
            String value = criteria.get(key);
            
            output.writeUTF(key);
            output.writeUTF(value);
         }
      }
      output.flush();
      
      byte[] array = buffer.toByteArray();
      return new MessageEnvelope(process, SCOPE.code, array, 0, array.length);
   }
}
