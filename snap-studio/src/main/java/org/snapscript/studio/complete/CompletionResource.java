package org.snapscript.studio.complete;

import java.io.PrintStream;

import lombok.extern.slf4j.Slf4j;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.studio.common.resource.Resource;
import org.snapscript.studio.common.resource.ResourcePath;
import org.snapscript.studio.index.complete.CompletionCompiler;
import org.snapscript.studio.index.complete.CompletionRequest;
import org.snapscript.studio.index.complete.CompletionResponse;
import org.snapscript.studio.index.complete.FindConstructorsInScope;
import org.snapscript.studio.index.complete.FindForExpression;
import org.snapscript.studio.index.complete.FindInScopeMatching;
import org.snapscript.studio.index.complete.FindPossibleImports;
import org.snapscript.studio.index.complete.FindTraitToImplement;
import org.snapscript.studio.index.complete.FindTypesToExtend;
import org.snapscript.studio.project.Project;
import org.snapscript.studio.project.Workspace;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

// /complete/<project>
@Slf4j
@Component
@ResourcePath("/complete.*")
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
      CompletionRequest context = gson.fromJson(content, CompletionRequest.class);
      CompletionCompiler compiler = new CompletionCompiler(project.getIndexDatabase(),
            FindForExpression.class,
            FindInScopeMatching.class,
            FindConstructorsInScope.class,
            FindPossibleImports.class,
            FindTypesToExtend.class,
            FindTraitToImplement.class);
      
      CompletionResponse result = compiler.compile(context);
      String expression = result.getExpression();
      String details = result.getDetails();     
      String text = gson.toJson(result);
      
      response.setContentType("application/json");
      out.println(text);
      out.close();
      log.debug(expression);
      log.debug(details);
   }
}