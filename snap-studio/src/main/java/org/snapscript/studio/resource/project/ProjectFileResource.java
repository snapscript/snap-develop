package org.snapscript.studio.resource.project;

import java.io.OutputStream;
import java.io.PrintStream;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.snapscript.core.Reserved;
import org.snapscript.studio.Workspace;
import org.snapscript.studio.resource.ContentTypeResolver;
import org.snapscript.studio.resource.Resource;

public class ProjectFileResource implements Resource {
   
   private final ContentTypeResolver resolver;
   private final ProjectFileCache cache;
   private final Workspace workspace;
   
   public ProjectFileResource(Workspace workspace, ContentTypeResolver resolver){
      this.cache = new ProjectFileCache(workspace);
      this.workspace = workspace;
      this.resolver = resolver;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      Path path = request.getPath(); 
      String projectPath = path.getPath(2); // /<project-name>/<project-path> or /default/blah.snap
      ProjectFile projectFile = cache.getFile(path);
      OutputStream stream = response.getOutputStream();
      String type = resolver.resolveType(projectPath);
      String method = request.getMethod();
      
      response.setStatus(Status.OK);
      response.setContentType(type);

      if(workspace.getLogger().isTrace()) {
         workspace.getLogger().trace(method + ": " + path);
      }
      try {
         byte[] resource = projectFile.getByteArray();
         
         stream.write(resource);
         stream.close();
      }catch(Exception e) {
         PrintStream out = response.getPrintStream();
         response.setStatus(Status.NOT_FOUND);
         
         if(projectPath.endsWith(Reserved.SCRIPT_EXTENSION)){
            out.println("// No source found for " + projectPath);
         }
         out.close();
      }
   }
}