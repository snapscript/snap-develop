

package org.snapscript.develop.find;

import java.io.PrintStream;
import java.util.List;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.common.ThreadPool;
import org.snapscript.develop.resource.Resource;
import org.snapscript.develop.resource.project.ProjectBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TextMatchResource implements Resource {
   
   private final TextMatchScanner scanner;
   private final Gson gson;
   
   public TextMatchResource(ProjectBuilder builder, ProcessLogger logger, ThreadPool pool) {
      this.scanner = new TextMatchScanner(builder, logger, pool);
      this.gson = new GsonBuilder().setPrettyPrinting().create();
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      String pattern = request.getParameter("pattern");
      String query = request.getParameter("expression");
      Path path = request.getPath();
      PrintStream out = response.getPrintStream(8192);
      List<TextMatch> matches = scanner.scanFiles(path, pattern, query);
      String text = gson.toJson(matches);
      response.setContentType("application/json");
      out.println(text);
      out.close();
   }

}
