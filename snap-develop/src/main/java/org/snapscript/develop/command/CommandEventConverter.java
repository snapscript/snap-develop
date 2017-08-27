package org.snapscript.develop.command;

import lombok.AllArgsConstructor;

import org.snapscript.agent.event.BeginEvent;
import org.snapscript.agent.event.ExitEvent;
import org.snapscript.agent.event.PongEvent;
import org.snapscript.agent.event.ProfileEvent;
import org.snapscript.agent.event.RegisterEvent;
import org.snapscript.agent.event.ScopeEvent;
import org.snapscript.agent.event.SyntaxErrorEvent;
import org.snapscript.agent.event.WriteErrorEvent;
import org.snapscript.agent.event.WriteOutputEvent;
import org.snapscript.develop.common.TextEscaper;

@AllArgsConstructor
public class CommandEventConverter {
   
   private final CommandFilter filter;
   private final String project;

   public ScopeCommand convert(ScopeEvent event) throws Exception {
      return ScopeCommand.builder()
            .process(event.getProcess())
            .variables(event.getVariables().getLocal())
            .evaluation(event.getVariables().getEvaluation())
            .change(event.getVariables().getChange())
            .thread(event.getThread())
            .stack(event.getStack())
            .instruction(event.getInstruction())
            .status(event.getStatus())
            .resource(event.getResource())
            .line(event.getLine())
            .depth(event.getDepth())
            .key(event.getKey())
            .build();
   }
   
   public PrintErrorCommand convert(WriteErrorEvent event) throws Exception { 
      byte[] array = event.getData();
      int length = event.getLength();
      int offset = event.getOffset();
      return PrintErrorCommand.builder()
            .process(event.getProcess())
            .text(TextEscaper.escape(array, offset, length))
            .build();
   }
   
   public PrintOutputCommand convert(WriteOutputEvent event) throws Exception {  
      byte[] array = event.getData();
      int length = event.getLength();
      int offset = event.getOffset();
      return PrintOutputCommand.builder()
            .process(event.getProcess())
            .text(TextEscaper.escape(array, offset, length))
            .build();
   }
   
   public ProblemCommand convert(SyntaxErrorEvent event) throws Exception {
      return ProblemCommand.builder()
            .project(project)
            .description(event.getDescription())
            .resource(event.getResource())
            .time(System.currentTimeMillis())
            .line(event.getLine())
            .build();
   }
   
   public BeginCommand convert(BeginEvent event) throws Exception {
      return BeginCommand.builder()
            .process(event.getProcess())
            .resource(event.getResource())
            .duration(event.getDuration())
            .debug(event.isDebug())
            .build();
   }
   
   public ProfileCommand convert(ProfileEvent event) throws Exception {
      return ProfileCommand.builder()
            .process(event.getProcess())
            .results(event.getResults())
            .build();
   }
   
   public StatusCommand convert(RegisterEvent event) throws Exception {        
      String focus = filter.getFocus();
      String process = event.getProcess();
      return StatusCommand.builder()
            .process(process)
            .system(event.getSystem())
            .project(null)
            .resource(null)
            .time(System.currentTimeMillis())
            .debug(false)
            .running(false)
            .focus(process.equals(focus))
            .build();
   }
   
   public StatusCommand convert(PongEvent event) throws Exception { 
      String focus = filter.getFocus();
      String process = event.getProcess();
      return StatusCommand.builder()
            .process(process)
            .system(event.getSystem())
            .project(event.getProject())
            .resource(event.getResource())
            .time(System.currentTimeMillis())
            .debug(event.isDebug())
            .running(event.isRunning())
            .focus(process.equals(focus))
            .build();
   }
   
   public ExitCommand convert(ExitEvent event) throws Exception {  
      return ExitCommand.builder()
            .process(event.getProcess())
            .build();
   }
   
}