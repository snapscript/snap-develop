/*
 * WebContainer.java December 2016
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

package org.snapscript.develop.http;

import static org.simpleframework.http.Protocol.DATE;
import static org.simpleframework.http.Protocol.SERVER;

import java.util.UUID;

import org.simpleframework.http.Cookie;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebContainer implements Container {

   private static final Logger LOG = LoggerFactory.getLogger(WebContainer.class);

   private final Container container;
   private final String session;
   private final String name;

   public WebContainer(Container container, String name, String session) {
      this.container = container;
      this.session = session;
      this.name = name;
   }

   @Override
   public void handle(Request req, Response resp) {
      long time = System.currentTimeMillis();

      try {
         Cookie cookie = req.getCookie(session);
         
         if(cookie == null) {
            String value = UUID.randomUUID().toString();
            resp.setCookie(session, value);
         }
         resp.setDate(DATE, time);
         resp.setValue(SERVER, name);
         container.handle(req, resp);
      } catch (Throwable cause) {
         LOG.info("Internal server error", cause);

         try {
            resp.close();
         } catch (Exception ignore) {
            LOG.info("Could not close response", ignore);
         }
      }
   }

}
