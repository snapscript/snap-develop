package org.snapscript.studio.complete;

import java.util.Map;

import org.snapscript.studio.Workspace;
import org.snapscript.studio.resource.project.Project;

public class CompletionProcessor {
   
   private final CompletionCompiler builder;
   
   public CompletionProcessor(Workspace workspace) {
      this.builder = new CompletionCompiler(workspace);
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
      String name = project.getProjectName();
      int line = request.getLine();
      
      return new Completion(name, source, resource, prefix, complete, line);
   }
}