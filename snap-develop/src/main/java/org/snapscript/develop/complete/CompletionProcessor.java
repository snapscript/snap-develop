package org.snapscript.develop.complete;

import java.io.File;
import java.util.Map;

import org.snapscript.agent.ConsoleLogger;
import org.snapscript.develop.configuration.ConfigurationClassLoader;
import org.snapscript.develop.http.project.Project;

public class CompletionProcessor {
   
   private final CompletionCompiler builder;
   
   public CompletionProcessor(ConfigurationClassLoader loader, ConsoleLogger logger) {
      this.builder = new CompletionCompiler(loader, logger);
   }

   public Map<String, String> createTokens(CompletionRequest request, Project project) {
      CompletionMatcher finder = builder.compile();
      Completion state = createState(request, project);
      
      return finder.findTokens(state);
   }
   
   private Completion createState(CompletionRequest request, Project project){
      String prefix = request.getPrefix();
      String source = request.getSource();
      String resource = request.getResource();
      String complete = request.getComplete();
      File root = project.getProjectPath();
      int line = request.getLine();
      
      return new Completion(root, source, resource, prefix, complete, line);
   }
}
