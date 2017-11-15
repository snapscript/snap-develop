package org.snapscript.studio.complete;

import java.io.PrintStream;
import java.util.Map;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.common.thread.ThreadPool;
import org.snapscript.studio.Workspace;
import org.snapscript.studio.common.PatternEscaper;
import org.snapscript.studio.common.resource.Resource;
import org.snapscript.studio.resource.project.Project;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// /type/<project>
public class TypeNodeResource implements Resource {
   
   private static final String STAR_PATTERN = "_STAR_PATTERN_";
   private static final String EXPRESSION = "expression";

   private final TypeNodeScanner scanner;
   private final Workspace workspace;
   private final Gson gson;
   
   public TypeNodeResource(Workspace workspace, ThreadPool pool) {
      this.scanner = new TypeNodeScanner(workspace, pool);
      this.gson = new GsonBuilder().setPrettyPrinting().create();
      this.workspace = workspace;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      String expression = parse(request);
      PrintStream out = response.getPrintStream();
      Path path = request.getPath();
      Project project = workspace.createProject(path);
      Thread thread = Thread.currentThread();
      ClassLoader classLoader = project.getClassLoader();
      thread.setContextClassLoader(classLoader);
      Map<String, TypeNodeReference> tokens = scanner.findTypesIncludingClasses(path, expression);
      String text = gson.toJson(tokens);
      response.setContentType("application/json");
      out.println(text);
      out.close();
   }
   
   private String parse(Request request) {      
      String expression = request.getParameter(EXPRESSION);
      
      if(expression != null && !expression.isEmpty()) {
         expression = expression.replace("*", STAR_PATTERN);
         expression = PatternEscaper.escape(expression);
         expression = expression.replace(STAR_PATTERN, ".*");
         return expression + ".*";
      }
      return ".*";
   }
}