package org.snapscript.agent.debug;

import static org.snapscript.agent.event.ScopeEvent.RUNNING;
import static org.snapscript.agent.event.ScopeEvent.SUSPENDED;

import java.util.Collections;
import java.util.Map;

import org.snapscript.agent.event.ScopeEvent;
import org.snapscript.core.trace.TraceType;

public class ScopeEventBuilder {

   private final ScopeExtractor extractor;
   private final TraceType type;
   private final String process;
   private final String resource;
   private final String thread;
   private final String stack;
   private final int line;
   private final int depth;
   private final int count;
   
   public ScopeEventBuilder(ScopeExtractor extractor, TraceType type, String process, String thread, String stack, String resource, int line, int depth, int count) {
      this.extractor = extractor;
      this.process = process;
      this.thread = thread;
      this.resource = resource;
      this.stack = stack;
      this.line = line;
      this.depth = depth;
      this.count = count;
      this.type = type;
   }
   
   public ScopeEvent suspendEvent() {
      Map<String, Map<String, String>> variables = extractor.build();
      String name = type.name();
      
      return new ScopeEvent(process, thread, stack, name, SUSPENDED, resource, line, depth, count, variables);
   }
   
   public ScopeEvent resumeEvent() {
      Map<String, Map<String, String>> variables = Collections.emptyMap();
      String name = type.name();
      
      return new ScopeEvent(process, thread, stack, name, RUNNING, resource, line, depth, count, variables);
   }
}
