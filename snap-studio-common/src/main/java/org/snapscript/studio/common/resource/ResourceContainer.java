package org.snapscript.studio.common.resource;

import static org.simpleframework.http.Method.CONNECT;
import static org.simpleframework.http.Protocol.DATE;
import static org.simpleframework.http.Status.OK;

import java.io.IOException;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.snapscript.studio.common.FileDirectorySource;
import org.springframework.stereotype.Component;

@Component
public class ResourceContainer implements Container {

   private final FileDirectorySource workspace;
   private final CombinationMatcher matcher;

   public ResourceContainer(CombinationMatcher matcher, FileDirectorySource workspace) {
      this.matcher = matcher;
      this.workspace = workspace;
   }

   @Override
   public void handle(Request request, Response response) {
      long time = System.currentTimeMillis();
      String method = request.getMethod();
      
      try {
         Resource resource = matcher.match(request, response);

         response.setDate(DATE, time);
         response.setStatus(Status.OK);
         resource.handle(request, response);
      } catch (Throwable cause) {
         cause.printStackTrace();
         workspace.getLogger().info("Error handling resource", cause);
      } finally {
         try {
            if(!method.equals(CONNECT)) {
               response.close();
            }
         } catch (IOException ignore) {
            workspace.getLogger().info("Could not close response", ignore);
         }
      }
   }
}