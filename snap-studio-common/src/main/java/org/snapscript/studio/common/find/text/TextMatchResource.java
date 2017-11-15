package org.snapscript.studio.common.find.text;

import java.io.PrintStream;
import java.util.List;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.common.thread.ThreadPool;
import org.snapscript.studio.common.FileDirectorySource;
import org.snapscript.studio.common.resource.Resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TextMatchResource implements Resource {
   
   private static final int MAX_COUNT = 1000;
   
   private final TextMatchQueryParser parser;
   private final TextMatchScanner scanner;
   private final Gson gson;
   
   public TextMatchResource(FileDirectorySource workspace, ThreadPool pool) {
      this.scanner = new TextMatchScanner(workspace.getLogger(), pool);
      this.gson = new GsonBuilder().setPrettyPrinting().create();
      this.parser = new TextMatchQueryParser(workspace);
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      TextMatchQuery query = parser.parse(request);
      PrintStream stream = response.getPrintStream(8192);
      List<TextMatch> matches = scanner.process(query);
      int length = matches.size();
      
      if(length > MAX_COUNT) {
         matches = matches.subList(0, MAX_COUNT);
      }
      String text = gson.toJson(matches);
      response.setContentType("application/json");
      stream.println(text);
      stream.close();
   }

}