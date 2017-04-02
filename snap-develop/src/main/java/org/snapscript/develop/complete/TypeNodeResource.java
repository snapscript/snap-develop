
package org.snapscript.develop.complete;

import java.io.PrintStream;
import java.util.Map;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.common.thread.ThreadPool;
import org.snapscript.develop.common.PatternEscaper;
import org.snapscript.develop.configuration.ConfigurationClassLoader;
import org.snapscript.develop.resource.Resource;
import org.snapscript.develop.resource.project.ProjectBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// /type/<project>
public class TypeNodeResource implements Resource {
   
   private static final String STAR_PATTERN = "_STAR_PATTERN_";
   private static final String EXPRESSION = "expression";

   private final ConfigurationClassLoader loader;
   private final TypeNodeScanner scanner;
   private final Gson gson;
   
   public TypeNodeResource(ProjectBuilder builder, ConfigurationClassLoader loader, ProcessLogger logger, ThreadPool pool) {
      this.scanner = new TypeNodeScanner(builder, loader, logger, pool);
      this.gson = new GsonBuilder().setPrettyPrinting().create();
      this.loader = loader;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      String expression = parse(request);
      PrintStream out = response.getPrintStream();
      Path path = request.getPath();
      Thread thread = Thread.currentThread();
      ClassLoader classLoader = loader.getClassLoader();
      thread.setContextClassLoader(classLoader);
      Map<String, TypeNodeReference> tokens = scanner.findTypes(path, expression);
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
