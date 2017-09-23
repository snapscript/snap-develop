package org.snapscript.studio.resource;

import static org.simpleframework.http.Method.CONNECT;
import static org.simpleframework.http.Protocol.DATE;
import static org.simpleframework.http.Status.OK;

import java.io.IOException;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.snapscript.agent.log.ProcessLogger;

public class ResourceContainer implements Container {

   private final ResourceMatcher matcher;
   private final ProcessLogger logger;
   private final Resource failure;
   private final Status status;

   public ResourceContainer(ResourceMatcher matcher, ProcessLogger logger) {
      this(matcher, logger, OK);
   }

   public ResourceContainer(ResourceMatcher matcher, ProcessLogger logger, Status status) {
      this(matcher, logger, null, status);
   }

   public ResourceContainer(ResourceMatcher matcher, ProcessLogger logger, Resource failure) {
      this(matcher, logger, failure, OK);
   }

   public ResourceContainer(ResourceMatcher matcher, ProcessLogger logger, Resource failure, Status status) {
      this.failure = failure;
      this.matcher = matcher;
      this.logger = logger;
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
         logger.info("Error handling resource", cause);

         try {
            if (failure != null) {
               response.reset();
               failure.handle(request, response);
            }
         } catch (Throwable fatal) {
            logger.info("Could not send an error response", fatal);
         }
      } finally {
         try {
            if(!method.equals(CONNECT)) {
               response.close();
            }
         } catch (IOException ignore) {
            logger.info("Could not close response", ignore);
         }
      }
   }
}