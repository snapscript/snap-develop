package org.snapscript.studio.complete;

import java.io.PrintStream;
import java.util.Map;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.studio.configuration.ConfigurationClassLoader;
import org.snapscript.studio.resource.Resource;
import org.snapscript.studio.resource.project.Project;
import org.snapscript.studio.resource.project.ProjectBuilder;

import com.google.gson.Gson;

// /complete/<project>
public class CompletionResource implements Resource {

   private final ConfigurationClassLoader loader;
   private final CompletionProcessor completer;
   private final ProjectBuilder builder;
   private final Gson gson;
   
   public CompletionResource(ProjectBuilder builder, ConfigurationClassLoader loader, ProcessLogger logger) {
      this.completer = new CompletionProcessor(loader, logger);
      this.gson = new Gson();
      this.builder = builder;
      this.loader = loader;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      CompletionResponse result = new CompletionResponse();
      PrintStream out = response.getPrintStream();
      String content = request.getContent();
      Path path = request.getPath();
      Thread thread = Thread.currentThread();
      ClassLoader classLoader = loader.getClassLoader();
      thread.setContextClassLoader(classLoader);
      Project project = builder.createProject(path);
      CompletionRequest context = gson.fromJson(content, CompletionRequest.class);
      Map<String, String> tokens = completer.createTokens(context, project);
      result.setTokens(tokens);
      String text = gson.toJson(result);
      response.setContentType("application/json");
      out.println(text);
      out.close();
   }
}