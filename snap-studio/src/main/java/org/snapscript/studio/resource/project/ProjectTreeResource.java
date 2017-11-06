package org.snapscript.studio.resource.project;

import java.io.File;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.simpleframework.http.Cookie;
import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.studio.Workspace;
import org.snapscript.studio.resource.Resource;
import org.snapscript.studio.resource.display.DisplayModelResolver;
import org.snapscript.studio.resource.tree.TreeBuilder;
import org.snapscript.studio.resource.tree.TreeContext;
import org.snapscript.studio.resource.tree.TreeContextManager;

public class ProjectTreeResource implements Resource {
   
   private final TreeContextManager contextManager;
   private final AtomicInteger sessionCounter;
   private final TreeBuilder treeBuilder;
   private final Workspace workspace;
   private final String session;
   
   public ProjectTreeResource(Workspace workspace, TreeContextManager contextManager, DisplayModelResolver modelResolver, String session) {
      this.treeBuilder = new TreeBuilder(modelResolver);
      this.sessionCounter = new AtomicInteger();
      this.contextManager = contextManager;
      this.workspace = workspace;
      this.session = session;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      String name = request.getParameter("id");
      String expand = request.getParameter("expand");
      String folders = request.getParameter("folders");
      String depth = request.getParameter("depth");
      Path path = request.getPath(); // /tree/<project-name>
      String[] segments = path.getSegments();
      File treePath = workspace.getRoot();
      boolean foldersOnly = false;
      boolean isProject = false;
      int folderDepth = Integer.MAX_VALUE;
      
      if(segments.length > 1) {
         Project project = workspace.createProject(path);
         treePath = project.getProjectPath();
         isProject = true;
      }
      if(depth != null) {
         folderDepth = Integer.parseInt(depth);
      }
      if(folders != null) {
         foldersOnly = Boolean.parseBoolean(folders);
      }
      int count = sessionCounter.getAndIncrement();
      Cookie cookie = request.getCookie(session);
      String value = String.valueOf(count);
      
      if(cookie != null) {
         value = cookie.getValue();
      } else {
         response.setCookie(session, value);
      }
      String projectName = treePath.getName();
      TreeContext context = contextManager.getContext(treePath, projectName, value, isProject);

      context.folderExpand(expand);
      String result = treeBuilder.createTree(context, name, foldersOnly, folderDepth);
      PrintStream out = response.getPrintStream();
      response.setContentType("text/html");
      out.println(result);
      out.close();
   }
}