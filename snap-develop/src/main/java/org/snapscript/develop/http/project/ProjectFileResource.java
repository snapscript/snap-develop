package org.snapscript.develop.http.project;

import java.io.OutputStream;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
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
      byte[] resource = fileSystem.readAsByteArray(projectPath);
      String type = resolver.resolveType(projectPath);
      OutputStream out = response.getOutputStream();
      
      response.setContentType(type);
      out.write(resource);
      out.close();
   }
}
