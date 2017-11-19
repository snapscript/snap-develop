package org.snapscript.studio.complete;

import java.io.PrintStream;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.studio.Workspace;
import org.snapscript.studio.agent.log.ProcessLogger;
import org.snapscript.studio.common.resource.Resource;
import org.snapscript.studio.index.complete.CompletionCompiler;
import org.snapscript.studio.index.complete.CompletionRequest;
import org.snapscript.studio.index.complete.CompletionResponse;
import org.snapscript.studio.index.complete.FindConstructorsInScope;
import org.snapscript.studio.index.complete.FindForFunction;
import org.snapscript.studio.index.complete.FindForVariable;
import org.snapscript.studio.index.complete.FindInScopeMatching;
import org.snapscript.studio.index.complete.FindPossibleImports;
import org.snapscript.studio.resource.project.Project;

import com.google.gson.Gson;

// /complete/<project>
public class CompletionResource implements Resource {

   private final Workspace workspace;
   private final Gson gson;
   
   public CompletionResource(Workspace workspace) {
      this.gson = new Gson();
      this.workspace = workspace;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      PrintStream out = response.getPrintStream();
      String content = request.getContent();
      Path path = request.getPath();
      Project project = workspace.createProject(path);
      ClassLoader classLoader = project.getClassLoader();
      Thread thread = Thread.currentThread();
      thread.setContextClassLoader(classLoader);
      ProcessLogger logger = workspace.getLogger();
      CompletionRequest context = gson.fromJson(content, CompletionRequest.class);
      CompletionCompiler compiler = new CompletionCompiler(project.getIndexDatabase(),
            FindForFunction.class,
            FindForVariable.class,
            FindInScopeMatching.class,
            FindConstructorsInScope.class,
            FindPossibleImports.class);
      
      CompletionResponse results = compiler.compile(context);
      String details = results.getDetails();     
      String text = gson.toJson(results);
      
      response.setContentType("application/json");
      out.println(text);
      out.close();
      logger.debug(details);
   }
}