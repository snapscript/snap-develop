package org.snapscript.studio.resource;

import static org.simpleframework.http.Method.CONNECT;
import static org.simpleframework.http.Protocol.DATE;
import static org.simpleframework.http.Status.OK;

import java.io.IOException;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.snapscript.studio.Workspace;

public class ResourceContainer implements Container {

   private final ResourceMatcher matcher;
   private final Workspace workspace;
   private final Resource failure;
   private final Status status;

   public ResourceContainer(ResourceMatcher matcher, Workspace workspace) {
      this(matcher, workspace, OK);
   }

   public ResourceContainer(ResourceMatcher matcher, Workspace workspace, Status status) {
      this(matcher, workspace, null, status);
   }

   public ResourceContainer(ResourceMatcher matcher, Workspace workspace, Resource failure) {
      this(matcher, workspace, failure, OK);
   }

   public ResourceContainer(ResourceMatcher matcher, Workspace workspace, Resource failure, Status status) {
      this.failure = failure;
      this.matcher = matcher;
      this.workspace = workspace;
      this.status = status;
   }

   @Override
   public void handle(Request request, Response response) {
      long time = System.currentTimeMillis();
      String method = request.getMethod();
      
      try {
         Resource resource = matcher.match(request, response);

         response.setDate(DATE, time);
         response.setCode(status.code);
         response.setDescription(status.description);
         resource.handle(request, response);
      } catch (Throwable cause) {
         cause.printStackTrace();
         workspace.getLogger().info("Error handling resource", cause);

         try {
            if (failure != null) {
               response.reset();
               failure.handle(request, response);
            }
         } catch (Throwable fatal) {
            workspace.getLogger().info("Could not send an error response", fatal);
         }
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