

package org.snapscript.develop.find.file;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.develop.resource.Resource;
import org.snapscript.develop.resource.project.ProjectBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FileMatchResource implements Resource {

   private final FileMatchQueryParser parser;
   private final FileMatchScanner scanner;
   private final Gson gson;
   
   public FileMatchResource(ProjectBuilder builder) {
      this.parser = new FileMatchQueryParser(builder);
      this.scanner = new FileMatchScanner();
      this.gson = new GsonBuilder().setPrettyPrinting().create();
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      FileMatchQuery query = parser.parse(request);
      String name = query.getProject();
      File directory = query.getPath();
      String expression = query.getQuery();
      PrintStream out = response.getPrintStream(8192);
      List<FileMatch> matches = scanner.findAllFiles(directory, name, expression);
      String text = gson.toJson(matches);
      response.setContentType("application/json");
      out.println(text);
      out.close();
   }
}
