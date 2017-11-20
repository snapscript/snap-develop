package org.snapscript.studio.resource.project;

import java.io.File;
import java.io.PrintStream;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.studio.common.resource.Resource;
import org.snapscript.studio.common.resource.ResourcePath;
import org.snapscript.studio.common.resource.display.DisplayModelResolver;
import org.snapscript.studio.common.resource.template.TemplateEngine;
import org.snapscript.studio.common.resource.template.TemplateModel;
import org.snapscript.studio.project.Workspace;
import org.springframework.stereotype.Component;

@Component
@ResourcePath("/")
public class ProjectSelectResource implements Resource {
   
   private static final String SELECT_RESOURCE = "select.vm";
   
   private final DisplayModelResolver resolver;
   private final TemplateEngine engine;
   private final Workspace workspace;
   
   public ProjectSelectResource(DisplayModelResolver resolver, Workspace workspace, TemplateEngine engine) {
      this.resolver = resolver;
      this.workspace = workspace;
      this.engine = engine;
   }
   
   @Override
   public void handle(Request request, Response response) throws Exception {
      TemplateModel model = resolver.getModel();
      File root = workspace.getRoot();
      String name = root.getName();
      model.setAttribute("root", name);
      String text = engine.renderTemplate(model, SELECT_RESOURCE);
      PrintStream stream = response.getPrintStream();

      response.setContentType("text/html");
      stream.print(text);
      stream.close();
   }
}