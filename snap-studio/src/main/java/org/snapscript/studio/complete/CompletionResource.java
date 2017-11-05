package org.snapscript.studio.complete;

import java.io.PrintStream;
import java.util.Map;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.studio.Workspace;
import org.snapscript.studio.resource.Resource;
import org.snapscript.studio.resource.project.Project;

import com.google.gson.Gson;

// /complete/<project>
public class CompletionResource implements Resource {

   private final CompletionProcessor completer;
   private final Workspace workspace;
   private final Gson gson;
   
   public CompletionResource(Workspace workspace) {
      this.completer = new CompletionProcessor(workspace);
      this.gson = new Gson();
      this.workspace = workspace;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      CompletionResponse result = new CompletionResponse();
      PrintStream out = response.getPrintStream();
      String content = request.getContent();
      Path path = request.getPath();
      Project project = workspace.createProject(path);
      ClassLoader classLoader = project.getClassLoader();
      Thread thread = Thread.currentThread();
      thread.setContextClassLoader(classLoader);
      CompletionRequest context = gson.fromJson(content, CompletionRequest.class);
      Map<String, String> tokens = completer.createTokens(context, project);
      result.setTokens(tokens);
      String text = gson.toJson(result);
      response.setContentType("application/json");
      out.println(text);
      out.close();
   }
}