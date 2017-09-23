package org.snapscript.studio.resource.project;

import java.io.File;
import java.io.PrintStream;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.studio.resource.Resource;
import org.snapscript.studio.resource.display.DisplayModelResolver;
import org.snapscript.studio.resource.template.TemplateEngine;
import org.snapscript.studio.resource.template.TemplateModel;

public class ProjectSelectResource implements Resource {
   
   private final DisplayModelResolver resolver;
   private final ProjectBuilder builder;
   private final TemplateEngine engine;
   private final String resource;
   
   public ProjectSelectResource(DisplayModelResolver resolver, ProjectBuilder builder, TemplateEngine engine, String resource) {
      this.resolver = resolver;
      this.resource = resource;
      this.builder = builder;
      this.engine = engine;
   }
   
   @Override
   public void handle(Request request, Response response) throws Exception {
      TemplateModel model = resolver.getModel();
      File root = builder.getRoot();
      String name = root.getName();
      model.setAttribute("root", name);
      String text = engine.renderTemplate(model, resource);
      PrintStream stream = response.getPrintStream();

      response.setContentType("text/html");
      stream.print(text);
      stream.close();
   }
}