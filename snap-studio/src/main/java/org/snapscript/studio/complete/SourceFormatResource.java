package org.snapscript.studio.complete;

import java.io.PrintStream;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.studio.common.resource.Resource;
import org.snapscript.studio.common.resource.ResourcePath;
import org.snapscript.studio.project.Project;
import org.snapscript.studio.project.Workspace;
import org.springframework.stereotype.Component;

// /format/<project>
@Component
@ResourcePath("/format.*")
public class SourceFormatResource implements Resource {
   
   private static final int DEFAULT_INDENT = 3;

   private final SourceFormatter formatter;
   private final Workspace workspace;
   
   public SourceFormatResource(Workspace workspace) {
      this.formatter = new SourceFormatter();
      this.workspace = workspace;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      PrintStream out = response.getPrintStream();
      String content = request.getContent();
      Path path = request.getPath();
      String token = request.getParameter("indent");
      Integer indent = token == null ? DEFAULT_INDENT : Integer.parseInt(token);
      Project project = workspace.createProject(path);
      String result = formatter.format(project, content, indent);
      response.setContentType("text/plain");
      out.println(result);
      out.close();
   }
}