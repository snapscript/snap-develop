package org.snapscript.develop.http.project;

import java.io.File;
import java.io.PrintStream;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.develop.http.resource.Resource;
import org.snapscript.develop.http.tree.TreeBuilder;

public class ProjectTreeResource implements Resource {
   
   private final ProjectBuilder builder;
   
   public ProjectTreeResource(ProjectBuilder builder) {
      this.builder = builder;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      String name = request.getParameter("id");
      String expand = request.getParameter("expand");
      String folders = request.getParameter("folders");
      String depth = request.getParameter("depth");
      Path path = request.getPath(); // /tree/<project-name>
      String[] segments = path.getSegments();
      File treePath = builder.getRoot();
      boolean foldersOnly = false;
      int folderDepth = Integer.MAX_VALUE;
      
      if(segments.length > 1) {
         Project project = builder.createProject(path);
         treePath = project.getProjectPath();
      }
      if(depth != null) {
         folderDepth = Integer.parseInt(depth);
      }
      if(folders != null) {
         foldersOnly = Boolean.parseBoolean(folders);
      }
      String result = TreeBuilder.createTree(treePath, name, expand, foldersOnly, folderDepth);
      PrintStream out = response.getPrintStream();
      response.setContentType("text/html");
      out.println(result);
      out.close();
   }
}
