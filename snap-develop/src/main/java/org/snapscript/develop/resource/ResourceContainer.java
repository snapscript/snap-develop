/*
 * ResourceContainer.java December 2016
 *
 * Copyright (C) 2016, Niall Gallagher <niallg@users.sf.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

package org.snapscript.develop.resource;

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
