package org.snapscript.studio.complete;

import java.io.PrintStream;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.studio.resource.Resource;
import org.snapscript.studio.resource.project.Project;
import org.snapscript.studio.resource.project.ProjectBuilder;

// /format/<project>
public class SourceFormatResource implements Resource {
   
   private static final int DEFAULT_INDENT = 3;

   private final SourceFormatter formatter;
   private final ProjectBuilder builder;
   
   public SourceFormatResource(ProjectBuilder builder) {
      this.formatter = new SourceFormatter();
      this.builder = builder;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      PrintStream out = response.getPrintStream();
      String content = request.getContent();
      Path path = request.getPath();
      String token = request.getParameter("indent");
      Integer indent = token == null ? DEFAULT_INDENT : Integer.parseInt(token);
      Project project = builder.createProject(path);
      String result = formatter.format(project, content, indent);
      response.setContentType("text/plain");
      out.println(result);
      out.close();
   }
}