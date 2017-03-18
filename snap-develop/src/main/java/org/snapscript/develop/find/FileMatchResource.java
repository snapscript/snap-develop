

package org.snapscript.develop.find;

import java.io.PrintStream;
import java.util.List;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.develop.resource.Resource;
import org.snapscript.develop.resource.project.ProjectBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FileMatchResource implements Resource {

   private final FileMatchScanner scanner;
   private final Gson gson;
   
   public FileMatchResource(ProjectBuilder builder) {
      this.scanner = new FileMatchScanner(builder);
      this.gson = new GsonBuilder().setPrettyPrinting().create();
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      String query = request.getParameter("expression");
      Path path = request.getPath();
      PrintStream out = response.getPrintStream(8192);
      List<FileMatch> matches = scanner.findAllFiles(path, query);
      String text = gson.toJson(matches);
      response.setContentType("application/json");
      out.println(text);
      out.close();
   }
}
