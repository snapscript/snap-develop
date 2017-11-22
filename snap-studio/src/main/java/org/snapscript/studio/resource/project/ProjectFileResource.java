package org.snapscript.studio.resource.project;

import java.io.OutputStream;
import java.io.PrintStream;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.snapscript.core.Reserved;
import org.snapscript.studio.common.resource.ContentTypeResolver;
import org.snapscript.studio.common.resource.Resource;
import org.snapscript.studio.common.resource.ResourcePath;
import org.snapscript.studio.project.FileCache;
import org.snapscript.studio.project.FileData;
import org.snapscript.studio.project.Project;
import org.snapscript.studio.project.Workspace;
import org.springframework.stereotype.Component;

@Component
@ResourcePath("/resource/.*")
public class ProjectFileResource implements Resource {
   
   private final ContentTypeResolver resolver;
   private final FileCache cache;
   private final Workspace workspace;

   public ProjectFileResource(Workspace workspace, ContentTypeResolver resolver){
      this.cache = new FileCache(workspace);
      this.workspace = workspace;
      this.resolver = resolver;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      Path path = request.getPath();
      Project project = workspace.getProject(path);
      String projectName = project.getProjectName();
      String projectPath = getPath(project, request); // /<project-name>/<project-path> or /default/blah.snap
      FileData projectFile = cache.getFile(projectName, projectPath);
      OutputStream stream = response.getOutputStream();
      String type = resolver.resolveType(projectPath);
      String method = request.getMethod();
      
      response.setStatus(Status.OK);
      response.setContentType(type);

      if(workspace.getLogger().isTraceEnabled()) {
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
   
   protected String getPath(Project project, Request request) throws Exception {
      Path path = request.getPath(); 
      String projectPath = path.getPath(2); // /<project-name>/<project-path> or /default/blah.snap
      
      if(isDownload()) {
         return project.getRealPath(projectPath);
      }
      return projectPath;
   }
   
   protected boolean isDownload(){
      return false;
   }
}