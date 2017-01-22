package org.snapscript.agent.debug;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.snapscript.agent.ConsoleLogger;
import org.snapscript.core.Context;
import org.snapscript.core.Module;
import org.snapscript.core.Path;
import org.snapscript.core.Scope;
import org.snapscript.core.State;
import org.snapscript.core.error.InternalErrorBuilder;
import org.snapscript.core.error.InternalError;
import org.snapscript.core.stack.ThreadStack;
import org.snapscript.core.trace.Trace;

public class ErrorStateExtractor extends TraceAdapter {
   
   private static final String INDENT = "   ";

   private final ConsoleLogger logger;
   
   public ErrorStateExtractor(ConsoleLogger logger) {
      this.logger = logger;
   }

   @Override
   public void error(Scope scope, Trace trace, Exception cause) {
      Module module = scope.getModule();
      Context context = module.getContext();
      ThreadStack stack = context.getStack();
      State state = scope.getState();
      Iterator<String> iterator = state.iterator();
      
      if(iterator.hasNext() && logger.isDebug()) {
         Set<String> expand = new HashSet<String>();
         StringWriter builder = new StringWriter();
         PrintWriter writer = new PrintWriter(builder);
         InternalErrorBuilder converter = new InternalErrorBuilder(stack, true);
         ScopeNodeTraverser traverser = new ScopeNodeTraverser(context, scope); 
         
         while(iterator.hasNext()) {
            String name = iterator.next();
            expand.add(name+".*");
         }
         Map<String, Map<String, String>> variables = traverser.expand(expand);
         Thread thread = Thread.currentThread();
         Path path = trace.getPath();
         int line = trace.getLine();
         InternalError exception = converter.create(cause);
         String descripton = createDescription(cause, thread, path, line);
         String error = createException(exception, INDENT);
         String data = createVariables(variables, INDENT);
         
         writer.print(descripton);
         writer.print(error);
         writer.print(data);
         
         String message = builder.toString();
         logger.info(message);
      }
   }
   
   private String createDescription(Exception cause, Thread thread, Path path, int line) {
      StringWriter builder = new StringWriter();
      PrintWriter writer = new PrintWriter(builder);
      
      writer.print("ERROR: '");
      writer.print(path);
      writer.print("' at line ");
      writer.print(line);
      writer.print(" on thread '");
      
      String name = thread.getName();
      
      writer.print(name);
      writer.print("' [");
      writer.print(cause);
      writer.println("]");
      writer.flush();
      
      return builder.toString();
   }
   
   private String createVariables(Map<String, Map<String, String>> variables, String indent) {
      StringWriter builder = new StringWriter();
      PrintWriter writer = new PrintWriter(builder);
      
      if(!variables.isEmpty()) {
         Set<String> names = variables.keySet();
         
         for(String name : names) {
            Map<String, String> data = variables.get(name);
            Set<String> keys = data.keySet();
    
            writer.print(indent);
            writer.print("name: ");
            writer.print(name);
            writer.println();
            
            for(String key : keys) {
               String value = data.get(key);
            
               writer.print(indent);
               writer.print(indent);
               writer.print(key);
               writer.print(": ");
               writer.print(value);
               writer.println();
            }
         }
         writer.flush();
      }
      return builder.toString();
   }
   
   private String createException(InternalError error, String indent) {
      StringWriter builder = new StringWriter();
      PrintWriter writer = new PrintWriter(builder);
      
      Object cause = error.getValue();
      
      if(Throwable.class.isInstance(cause)) {
         Throwable throwable = (Throwable)cause;
      
         throwable.printStackTrace(writer);
         writer.flush();
      } else {
         error.printStackTrace(writer);
         writer.flush();
      }
      
      String text = builder.toString();
      String[] lines = text.split("\\r?\\n");
      StringBuffer buffer = builder.getBuffer();
      
      buffer.setLength(0);
      writer.print(indent);
      writer.print("cause: ");
      
      for(int i = 0; i < lines.length; i++) {
         String line = lines[i];
         
         if(i > 0) {
            writer.print(indent);
            writer.print(indent);
         }
         writer.println(line);
      }
      writer.flush();
      writer.close();
      
      return builder.toString();
   }
}
