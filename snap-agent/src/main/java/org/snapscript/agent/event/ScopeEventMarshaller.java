package org.snapscript.agent.event;

import static org.snapscript.agent.event.ProcessEventType.SCOPE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.snapscript.agent.debug.ScopeVariableTree;

public class ScopeEventMarshaller implements ProcessEventMarshaller<ScopeEvent> {
   
   private final ScopeMapMarshaller marshaller;
   
   public ScopeEventMarshaller() {
      this.marshaller = new ScopeMapMarshaller();
   }

   @Override
   public ScopeEvent fromMessage(MessageEnvelope message) throws IOException {
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
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
      int change = input.readInt();
      Map<String, Map<String, String>> local = marshaller.readMap(input);
      Map<String, Map<String, String>> evaluation = marshaller.readMap(input);
      
      ScopeVariableTree tree = new ScopeVariableTree.Builder(change)
         .withLocal(local)
         .withEvaluation(evaluation)
         .build();
     
      return new ScopeEvent.Builder(process)
         .withVariables(tree)
         .withThread(thread)
         .withStack(stack)
         .withInstruction(instruction)
         .withStatus(status)
         .withResource(resource)
         .withLine(line)
         .withDepth(depth)
         .withKey(sequence)
         .build();
      
   }

   @Override
   public MessageEnvelope toMessage(ScopeEvent event) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(buffer);
      ScopeVariableTree tree = event.getVariables();
      Map<String, Map<String, String>> local = tree.getLocal();
      Map<String, Map<String, String>> evaluation = tree.getEvaluation();
      String process = event.getProcess();
      String thread = event.getThread();
      String stack = event.getStack();
      String instruction = event.getInstruction();
      String status = event.getStatus();
      String resource = event.getResource();
      int change = tree.getChange();
      int sequence = event.getKey();
      int line = event.getLine();
      int depth = event.getDepth();
      
      output.writeUTF(process);
      output.writeUTF(thread);
      output.writeUTF(stack);
      output.writeUTF(instruction);
      output.writeUTF(status);
      output.writeUTF(resource);
      output.writeInt(line);
      output.writeInt(depth);
      output.writeInt(sequence);
      output.writeInt(change);
      marshaller.writeMap(output, local);
      marshaller.writeMap(output, evaluation);
      output.flush();
      
      byte[] array = buffer.toByteArray();
      return new MessageEnvelope(process, SCOPE.code, array, 0, array.length);
   }
   
   private static class ScopeMapMarshaller {
      
      public Map<String, Map<String, String>> readMap(DataInput input) throws IOException {
         Map<String, Map<String, String>> map = new TreeMap<String, Map<String, String>>();
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
            map.put(name, criteria);
         }
         return map;
      }
      
      public void writeMap(DataOutput output, Map<String, Map<String, String>> map) throws IOException {
         Set<String> names = map.keySet();
         int count = map.size();
         
         output.writeInt(count);
         
         for(String name : names) {
            Map<String, String> criteria = map.get(name);
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
      }
   }
}
