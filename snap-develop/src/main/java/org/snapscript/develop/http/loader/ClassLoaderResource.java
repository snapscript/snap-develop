package org.snapscript.develop.http.loader;

import java.io.PrintStream;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.develop.http.resource.Resource;

public class ClassLoaderResource implements Resource {
   
   private final ClassResourceLoader loader;
   private final ProcessLogger logger;

   public ClassLoaderResource(ClassResourceLoader loader, ProcessLogger logger) {
      this.logger = logger;
      this.loader = loader;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      String method = request.getMethod();
      Path path = request.getPath(); // /class/com/example/SomeClass.class
      String normal = path.getPath(1); // /com/example/SomeClass.class
      PrintStream output = response.getPrintStream();
      byte[] data = loader.loadClass(normal); 

      if(logger.isTrace()) {
         logger.trace(method + ": " + normal);
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
