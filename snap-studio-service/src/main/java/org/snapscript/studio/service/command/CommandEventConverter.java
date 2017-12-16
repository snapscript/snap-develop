package org.snapscript.studio.service.command;

import java.util.Set;

import lombok.AllArgsConstructor;

import org.snapscript.studio.agent.event.BeginEvent;
import org.snapscript.studio.agent.event.ExitEvent;
import org.snapscript.studio.agent.event.PongEvent;
import org.snapscript.studio.agent.event.ProfileEvent;
import org.snapscript.studio.agent.event.RegisterEvent;
import org.snapscript.studio.agent.event.ScopeEvent;
import org.snapscript.studio.agent.event.SyntaxErrorEvent;
import org.snapscript.studio.agent.event.WriteErrorEvent;
import org.snapscript.studio.agent.event.WriteOutputEvent;
import org.snapscript.studio.agent.profiler.ProfileResult;
import org.snapscript.studio.common.TextEscaper;
import org.snapscript.studio.project.Project;

@AllArgsConstructor
public class CommandEventConverter {
   
   private final CommandFilter filter;
   private final Project project;

   public ScopeCommand convert(ScopeEvent event) throws Exception {
      String resource = event.getResource();
      String path = project.getRealPath(resource);
      
      return ScopeCommand.builder()
            .process(event.getProcess())
            .variables(event.getVariables().getLocal())
            .evaluation(event.getVariables().getEvaluation())
            .change(event.getVariables().getChange())
            .thread(event.getThread())
            .stack(event.getStack())
            .instruction(event.getInstruction())
            .status(event.getStatus())
            .line(event.getLine())
            .depth(event.getDepth())
            .key(event.getKey())
            .resource(path)
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
      String resource = event.getResource();
      String path = project.getRealPath(resource);
      String name = project.getProjectName();
      
      return ProblemCommand.builder()
            .project(name)
            .description(event.getDescription())
            .time(System.currentTimeMillis())
            .line(event.getLine())
            .resource(path)
            .build();
   }
   
   public BeginCommand convert(BeginEvent event) throws Exception {
      String resource = event.getResource();
      String path = project.getRealPath(resource);
      
      return BeginCommand.builder()
            .process(event.getProcess())
            .duration(event.getDuration())
            .debug(event.isDebug())
            .resource(path)
            .build();
   }
   
   public ProfileCommand convert(ProfileEvent event) throws Exception {
      Set<ProfileResult> results = event.getResults();
      
      for(ProfileResult result : results) {
         String resource = result.getResource();
         String path = project.getRealPath(resource);
         
         result.setResource(path);
      }
      return ProfileCommand.builder()
            .process(event.getProcess())
            .results(results)
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
      String resource = event.getResource();
      String path = project.getRealPath(resource);
      
      return StatusCommand.builder()
            .process(process)
            .system(event.getSystem())
            .project(event.getProject())
            .time(System.currentTimeMillis())
            .debug(event.isDebug())
            .totalMemory(event.getTotalMemory())
            .usedMemory(event.getUsedMemory())
            .threads(event.getThreads())
            .running(event.isRunning())
            .focus(process.equals(focus))
            .resource(path)
            .build();
   }
   
   public ExitCommand convert(ExitEvent event) throws Exception {  
      return ExitCommand.builder()
            .process(event.getProcess())
            .build();
   }
   
}