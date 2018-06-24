package org.snapscript.studio.agent.event;

import static org.snapscript.studio.agent.event.ProcessEventType.EXECUTE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExecuteEventMarshaller implements ProcessEventMarshaller<ExecuteEvent> {

   @Override
   public ExecuteEvent fromMessage(MessageEnvelope message) throws IOException {
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
      ByteArrayInputStream buffer = new ByteArrayInputStream(array, offset, length);
      DataInputStream input = new DataInputStream(buffer);
      Map<String, Map<Integer, Boolean>> breakpoints = new HashMap<String, Map<Integer, Boolean>>();
      List<String> arguments = new ArrayList<String>();
      String process = input.readUTF();
      String project = input.readUTF();
      String resource = input.readUTF();
      String dependencies = input.readUTF();
      boolean debug = input.readBoolean();
      int breakpointSize = input.readInt();
      
      for(int i = 0; i < breakpointSize; i++) {
         Map<Integer, Boolean> locations = new HashMap<Integer, Boolean>();
         String script = input.readUTF();
         int locationSize = input.readInt();

         for(int j = 0; j < locationSize; j++) {
            int line = input.readInt();
            boolean enable = input.readBoolean();
            
            locations.put(line, enable);
         }
         breakpoints.put(script, locations);
      }
      int argumentSize = input.readInt();
      
      for(int i = 0; i < argumentSize; i++) {
         String argument = input.readUTF();
         arguments.add(argument);
      }
      return new ExecuteEvent.Builder(process)
         .withProject(project)
         .withResource(resource)
         .withDependencies(dependencies)
         .withBreakpoints(breakpoints)
         .withArguments(arguments)
         .withDebug(debug)
         .build();
   }

   @Override
   public MessageEnvelope toMessage(ExecuteEvent event) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(buffer);
      Map<String, Map<Integer, Boolean>> breakpoints = event.getBreakpoints();
      List<String> arguments = event.getArguments();
      Set<String> scripts = breakpoints.keySet();
      String process = event.getProcess();
      String resource = event.getResource();
      String dependences = event.getDependences();
      String project = event.getProject();
      boolean debug = event.isDebug();
      int breakpointSize = breakpoints.size();
      int argumentSize = arguments.size();
      
      output.writeUTF(process);
      output.writeUTF(project);
      output.writeUTF(resource);
      output.writeUTF(dependences);
      output.writeBoolean(debug);
      output.writeInt(breakpointSize);
      
      for(String script : scripts) {
         Map<Integer, Boolean> locations = breakpoints.get(script);
         Set<Integer> lines = locations.keySet();
         int locationSize = locations.size();
         
         output.writeUTF(script);
         output.writeInt(locationSize);
         
         for(Integer line : lines) {
            Boolean enable = locations.get(line);
            
            output.writeInt(line);
            output.writeBoolean(enable);
         }
      }
      output.writeInt(argumentSize);
      
      for(String argument : arguments){
         output.writeUTF(argument);
      }
      output.flush();
      byte[] array = buffer.toByteArray();
      return new MessageEnvelope(EXECUTE.code, array, 0, array.length);
   }

}