package org.snapscript.develop.http.project;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.develop.http.resource.Resource;
import org.snapscript.develop.http.resource.template.TemplateEngine;
import org.snapscript.develop.http.resource.template.TemplateModel;

public class ProjectOpenDialog implements Resource {
   
   private final ProjectBuilder builder;
   private final TemplateEngine engine;
   private final String resource;
   
   public ProjectOpenDialog(ProjectBuilder builder, TemplateEngine engine, String resource) {
      this.builder = builder;
      this.resource = resource;
      this.engine = engine;
   }
   
   @Override
   public void handle(Request request, Response response) throws Exception {
      Map<String, Object> map = new HashMap<String, Object>();
      TemplateModel model = new TemplateModel(map);
      File root = builder.getRoot();
      String name = root.getName();
      map.put("root", name);
      String text = engine.renderTemplate(model, resource);
      PrintStream stream = response.getPrintStream();

      response.setContentType("text/html");
      stream.print(text);
      stream.close();
   }
}
