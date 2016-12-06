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

import org.snapscript.agent.debug.ScopeVariableTree;

public class ScopeEventMarshaller implements ProcessEventMarshaller<ScopeEvent> {

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
      int evaluationCount = input.readInt();
      int localCount = input.readInt();

      Map<String, Map<String, String>> local = new TreeMap<String, Map<String, String>>();
      Map<String, Map<String, String>> evaluation = new TreeMap<String, Map<String, String>>();
      ScopeVariableTree tree = new ScopeVariableTree.Builder(change)
         .withLocal(local)
         .withEvaluation(evaluation)
         .build();
      
      for(int i = 0; i < localCount; i++) {
         Map<String, String> criteria = new HashMap<String, String>();
         String name = input.readUTF();
         int size = input.readInt();
         
         for(int j = 0; j < size; j++) {
            String key = input.readUTF();
            String value = input.readUTF();
            
            criteria.put(key, value);
         }
         local.put(name, criteria);
      }
      for(int i = 0; i < evaluationCount; i++) {
         Map<String, String> criteria = new HashMap<String, String>();
         String name = input.readUTF();
         int size = input.readInt();
         
         for(int j = 0; j < size; j++) {
            String key = input.readUTF();
            String value = input.readUTF();
            
            criteria.put(key, value);
         }
         evaluation.put(name, criteria);
      }
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
      Set<String> evaluationNames = evaluation.keySet();
      Set<String> localNames = local.keySet();
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
      int localCount = local.size();
      int evaluationCount = evaluation.size();
      
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
      output.writeInt(localCount);
      output.writeInt(evaluationCount);
      
      for(String name : localNames) {
         Map<String, String> criteria = local.get(name);
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
      for(String name : evaluationNames) {
         Map<String, String> criteria = evaluation.get(name);
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
