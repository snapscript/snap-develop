package org.snapscript.develop.http.resource;

import static org.simpleframework.http.Method.CONNECT;
import static org.simpleframework.http.Protocol.DATE;
import static org.simpleframework.http.Status.OK;

import java.io.IOException;

import org.simpleframework.http.Method;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceContainer implements Container {

   private static final Logger LOG = LoggerFactory.getLogger(ResourceContainer.class);

   private final ResourceMatcher matcher;
   private final Resource failure;
   private final Status status;

   public ResourceContainer(ResourceMatcher matcher) {
      this(matcher, OK);
   }

   public ResourceContainer(ResourceMatcher matcher, Status status) {
      this(matcher, null, status);
   }

   public ResourceContainer(ResourceMatcher matcher, Resource failure) {
      this(matcher, failure, OK);
   }

   public ResourceContainer(ResourceMatcher matcher, Resource failure, Status status) {
      this.failure = failure;
      this.matcher = matcher;
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
         LOG.info("Error handling resource", cause);

         try {
            if (failure != null) {
               response.reset();
               failure.handle(request, response);
            }
         } catch (Throwable fatal) {
            LOG.info("Could not send an error response", fatal);
         }
      } finally {
         try {
            if(!method.equals(CONNECT)) {
               response.close();
            }
         } catch (IOException ignore) {
            LOG.info("Could not close response", ignore);
         }
      }
   }
}
