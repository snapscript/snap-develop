package org.snapscript.develop.command;

import java.util.Map;
import java.util.Set;

import org.snapscript.agent.debug.ScopeVariableTree;
import org.snapscript.agent.event.BeginEvent;
import org.snapscript.agent.event.ExitEvent;
import org.snapscript.agent.event.PongEvent;
import org.snapscript.agent.event.ProcessEventAdapter;
import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.agent.event.ProfileEvent;
import org.snapscript.agent.event.RegisterEvent;
import org.snapscript.agent.event.ScopeEvent;
import org.snapscript.agent.event.SyntaxErrorEvent;
import org.snapscript.agent.event.WriteErrorEvent;
import org.snapscript.agent.event.WriteOutputEvent;
import org.snapscript.agent.profiler.ProfileResult;
import org.snapscript.develop.common.TextEscaper;

public class CommandEventForwarder extends ProcessEventAdapter {
   
   private final CommandFilter filter;
   private final CommandClient client;

   
   public CommandEventForwarder(CommandClient client, CommandFilter filter) {
      this.filter = filter;
      this.client = client;
   } 
   
   @Override
   public void onScope(ProcessEventChannel channel, ScopeEvent event) throws Exception {
      if(filter.accept(event)) {
         ScopeVariableTree tree = event.getVariables();
         Map<String, Map<String, String>> variables = tree.getVariables();
         String process = event.getProcess();
         String thread = event.getThread();
         String stack = event.getStack();
         String instruction = event.getInstruction();
         String status = event.getStatus();
         String resource = event.getResource();
         int change = tree.getChange();
         int depth = event.getDepth();
         int line = event.getLine();
         int key = event.getKey();
         client.sendScope(process, variables, thread, stack, instruction, status, resource, line, depth, key, change);
      }
   }
   
   @Override
   public void onWriteError(ProcessEventChannel channel, WriteErrorEvent event) throws Exception {   
      if(filter.accept(event)) {
         String process = event.getProcess();
         byte[] array = event.getData();
         int length = event.getLength();
         int offset = event.getOffset();
         String text = TextEscaper.escape(array, offset, length);
         client.sendPrintError(process, text);
      }
   }
   
   @Override
   public void onWriteOutput(ProcessEventChannel channel, WriteOutputEvent event) throws Exception {  
      if(filter.accept(event)) {
         String process = event.getProcess();
         byte[] array = event.getData();
         int length = event.getLength();
         int offset = event.getOffset();
         String text = TextEscaper.escape(array, offset, length);
         client.sendPrintOutput(process, text);
      }
   }
   
   @Override
   public void onSyntaxError(ProcessEventChannel channel, SyntaxErrorEvent event) throws Exception {
      if(filter.accept(event)) {
         String description = event.getDescription();
         String resource = event.getResource();
         int line = event.getLine();
         long time = System.currentTimeMillis();
         client.sendSyntaxError(resource, description, time, line);
      }
   }
   
   @Override
   public void onBegin(ProcessEventChannel channel, BeginEvent event) throws Exception {
      if(filter.accept(event)) {
         String process = event.getProcess();
         String resource = event.getResource();
         long duration = event.getDuration();
         client.sendBegin(process, resource, duration);
      }
   }
   
   @Override
   public void onProfile(ProcessEventChannel channel, ProfileEvent event) throws Exception {
      if(filter.accept(event)) {
         String process = event.getProcess();
         Set<ProfileResult> results = event.getResults();
         client.sendProfile(process, results);
      }
   }
   
   @Override
   public void onRegister(ProcessEventChannel channel, RegisterEvent event) throws Exception {  
      String focus = filter.get();
      String process = event.getProcess();
      String system = event.getSystem();
      long time = System.currentTimeMillis();
      client.sendStatus(process, system, null, time, false, process.equals(focus)); // update clients on status
   }
   
   @Override
   public void onPong(ProcessEventChannel channel, PongEvent event) throws Exception {  
      String focus = filter.get();
      String process = event.getProcess();
      String system = event.getSystem();
      String resource = event.getResource();
      boolean running = event.isRunning();
      long time = System.currentTimeMillis();
      client.sendStatus(process, system, resource, time, running, process.equals(focus)); // update clients on status
   }
   
   @Override
   public void onExit(ProcessEventChannel channel, ExitEvent event) throws Exception {  
      String process = event.getProcess();
      client.sendProcessExit(process);
   }
   
   @Override
   public void onClose(ProcessEventChannel channel) throws Exception { 
      String focus = filter.get();
      if(focus != null) {
         client.sendProcessTerminate(focus); 
      }
   }
}
