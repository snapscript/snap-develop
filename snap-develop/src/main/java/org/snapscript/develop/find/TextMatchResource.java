

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
      TextMatchRequest matchRequest = parse(request);
      PrintStream out = response.getPrintStream(8192);
      List<TextMatch> matches = scanner.scanFiles(matchRequest);
      String text = gson.toJson(matches);
      response.setContentType("application/json");
      out.println(text);
      out.close();
   }
   
   private TextMatchRequest parse(Request request) throws Throwable {
      Path path = request.getPath();
      String pattern = request.getParameter("pattern");
      String query = request.getParameter("expression");
      boolean caseSensitive = Boolean.parseBoolean(request.getParameter("caseSensitive"));
      boolean regularExpression = Boolean.parseBoolean(request.getParameter("regularExpression"));
      boolean wholeWord = Boolean.parseBoolean(request.getParameter("wholeWord"));
      
      return TextMatchRequest.builder()
            .pattern(pattern)
            .query(query)
            .path(path)
            .caseSensitive(caseSensitive)
            .regularExpression(regularExpression)
            .wholeWord(wholeWord)
            .build();
   }

}
