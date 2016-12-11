package org.snapscript.develop.http.project;

import java.io.OutputStream;
import java.io.PrintStream;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.snapscript.core.Reserved;
import org.snapscript.develop.http.resource.ContentTypeResolver;
import org.snapscript.develop.http.resource.Resource;

public class ProjectFileResource implements Resource {
   
   private final ContentTypeResolver resolver;
   private final ProjectBuilder builder;
   
   public ProjectFileResource(ProjectBuilder builder, ContentTypeResolver resolver){
      this.resolver = resolver;
      this.builder = builder;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      Path path = request.getPath(); 
      String projectPath = path.getPath(2); // /<project-name>/<project-path> or /default/blah.snap
      Project project = builder.createProject(path);
      ProjectFileSystem fileSystem = project.getFileSystem();
      String type = resolver.resolveType(projectPath);
      
      response.setStatus(Status.OK);
      response.setContentType(type);
      
      try {
         byte[] resource = fileSystem.readAsByteArray(projectPath);
         OutputStream out = response.getOutputStream();
         
         out.write(resource);
         out.close();
      }catch(Exception e) {
         PrintStream out = response.getPrintStream();
         response.setStatus(Status.NOT_FOUND);
         
         if(projectPath.endsWith(Reserved.SCRIPT_EXTENSION)){
            response.setStatus(Status.OK); // just to display .snap comment
            out.println("// No source found for " + projectPath);
         }
         out.close();
      }
   }
}
