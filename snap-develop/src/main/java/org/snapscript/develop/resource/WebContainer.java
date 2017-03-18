
package org.snapscript.develop.resource;

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
