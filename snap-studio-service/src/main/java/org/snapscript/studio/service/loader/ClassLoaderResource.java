package org.snapscript.studio.service.loader;

import java.io.PrintStream;

import lombok.extern.slf4j.Slf4j;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.snapscript.studio.common.resource.Resource;
import org.snapscript.studio.common.resource.ResourcePath;
import org.snapscript.studio.project.Workspace;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ResourcePath("/class/.*")
public class ClassLoaderResource implements Resource {
   
   private final ClassPathResourceLoader loader;
   private final Workspace workspace;

   public ClassLoaderResource(ClassPathResourceLoader loader, Workspace workspace) {
      this.workspace = workspace;
      this.loader = loader;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      String method = request.getMethod();
      Path path = request.getPath(); // /class/com/example/SomeClass.class
      String normal = path.getPath(1); // /com/example/SomeClass.class
      PrintStream output = response.getPrintStream();
      byte[] data = loader.loadResource(normal); 

      if(log.isTraceEnabled()) {
         log.trace(method + ": " + normal);
      }
      if(data == null) {
         response.setStatus(Status.NOT_FOUND);
         response.setContentType("text/plain");
         output.print("Class ");
         output.print(path);
         output.println(" could not be found");
      } else {
         response.setStatus(Status.OK);
         response.setContentType("application/octet-stream");
         response.setContentLength(data.length);
         output.write(data);
      }
      output.close();
   }
}