package org.snapscript.studio.service.command;

import java.util.Map;

import org.snapscript.studio.agent.debug.ScopeVariableTree;
import org.snapscript.studio.agent.event.BeginEvent;
import org.snapscript.studio.agent.event.ExitEvent;
import org.snapscript.studio.agent.event.FaultEvent;
import org.snapscript.studio.agent.event.PongEvent;
import org.snapscript.studio.agent.event.ProcessEventAdapter;
import org.snapscript.studio.agent.event.ProcessEventChannel;
import org.snapscript.studio.agent.event.ProfileEvent;
import org.snapscript.studio.agent.event.RegisterEvent;
import org.snapscript.studio.agent.event.ScopeEvent;
import org.snapscript.studio.agent.event.ScriptErrorEvent;
import org.snapscript.studio.agent.event.WriteErrorEvent;
import org.snapscript.studio.agent.event.WriteOutputEvent;
import org.snapscript.studio.project.Project;
import org.snapscript.studio.service.core.FaultLogger;

public class CommandEventForwarder extends ProcessEventAdapter {
   
   private final CommandEventConverter converter;
   private final CommandFilter filter;
   private final CommandClient client;
   private final FaultLogger logger;
   
   public CommandEventForwarder(CommandClient client, CommandFilter filter, Project project) {
      this.converter = new CommandEventConverter(filter, project);
      this.logger = new FaultLogger();
      this.filter = filter;
      this.client = client;
   } 
   
   @Override
   public void onScope(ProcessEventChannel channel, ScopeEvent event) throws Exception {
      if(filter.isFocused(event)) {
         ScopeCommand command = converter.convert(event);
         client.sendCommand(command);
      }
   }
   
   @Override
   public void onFault(ProcessEventChannel channel, FaultEvent event) throws Exception {
      if(filter.isFocused(event)) {
         ScopeVariableTree tree = event.getVariables();
         Map<String, Map<String, String>> local = tree.getLocal();
         String process = event.getProcess();
         String thread = event.getThread();
         String cause = event.getCause();
         String resource = event.getResource();
         int line = event.getLine();
         logger.log(process, local, resource, thread, cause, line);
      }
   }
   
   @Override
   public void onWriteError(ProcessEventChannel channel, WriteErrorEvent event) throws Exception {   
      PrintErrorCommand command = converter.convert(event);
      client.sendCommand(command);
   }
   
   @Override
   public void onWriteOutput(ProcessEventChannel channel, WriteOutputEvent event) throws Exception {  
      PrintOutputCommand command = converter.convert(event);
      client.sendCommand(command);
   }
   
   @Override
   public void onScriptError(ProcessEventChannel channel, ScriptErrorEvent event) throws Exception {
      ProblemCommand command = converter.convert(event);
      client.sendCommand(command);
   }
   
   @Override
   public void onBegin(ProcessEventChannel channel, BeginEvent event) throws Exception {
      if(filter.isFocused(event)) {
         BeginCommand command = converter.convert(event);
         client.sendCommand(command);
      }
   }
   
   @Override
   public void onProfile(ProcessEventChannel channel, ProfileEvent event) throws Exception {
      if(filter.isFocused(event)) {
         ProfileCommand command = converter.convert(event);
         client.sendCommand(command);
      }
   }
   
   @Override
   public void onRegister(ProcessEventChannel channel, RegisterEvent event) throws Exception {  
      StatusCommand command = converter.convert(event);
      client.sendCommand(command);
   }
   
   @Override
   public void onPong(ProcessEventChannel channel, PongEvent event) throws Exception {  
      StatusCommand command = converter.convert(event);
      client.sendCommand(command);
   }
   
   @Override
   public void onExit(ProcessEventChannel channel, ExitEvent event) throws Exception {  
      ExitCommand command = converter.convert(event);
      client.sendCommand(command);
   }
   
   @Override
   public void onClose(ProcessEventChannel channel) throws Exception { 
      String focus = filter.getFocus();
      if(focus != null) {
         TerminateCommand command = TerminateCommand.builder()
               .process(focus)
               .build();
         client.sendCommand(command); 
      }
   }
}