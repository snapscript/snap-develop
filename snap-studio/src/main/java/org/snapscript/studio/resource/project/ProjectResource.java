package org.snapscript.studio.resource.project;

import java.io.PrintStream;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.studio.common.resource.Resource;
import org.snapscript.studio.common.resource.template.TemplateEngine;
import org.snapscript.studio.common.resource.template.TemplateModel;
import org.snapscript.studio.resource.display.DisplayModelResolver;

public class ProjectResource implements Resource {
   
   private final DisplayModelResolver resolver;
   private final TemplateEngine engine;
   private final String resource;
   
   public ProjectResource(DisplayModelResolver resolver, TemplateEngine engine, String resource) {
      this.resource = resource;
      this.resolver = resolver;
      this.engine = engine;
   }
   
   @Override
   public void handle(Request request, Response response) throws Exception {
      TemplateModel model = resolver.getModel();
      Path path = request.getPath(); // /project/<project-name>/<project-path>
      String projectPrefix = path.getPath(1, 2); // /<project-name>
      String projectDirectory = path.getPath(1); // /<project-name>
      String projectName = projectPrefix.substring(1); // <project-name>
      model.setAttribute("project", projectName);
      model.setAttribute("projectDirectory", projectDirectory);
      String text = engine.renderTemplate(model, resource);
      PrintStream stream = response.getPrintStream();

      response.setContentType("text/html");
      stream.print(text);
      stream.close();
   }
}