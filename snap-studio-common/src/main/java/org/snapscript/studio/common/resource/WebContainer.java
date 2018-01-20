package org.snapscript.studio.common.resource;

import static org.snapscript.studio.common.resource.SessionConstants.SESSION_ID;
import static org.simpleframework.http.Protocol.DATE;
import static org.simpleframework.http.Protocol.SERVER;

import java.util.UUID;

import org.simpleframework.http.Cookie;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WebContainer implements Container {

   private static final Logger LOG = LoggerFactory.getLogger(WebContainer.class);

   private final WebSocketContainer container;

   public WebContainer(WebSocketContainer container) {
      this.container = container;
   }

   @Override
   public void handle(Request req, Response resp) {
      long time = System.currentTimeMillis();

      try {
         Cookie cookie = req.getCookie(SESSION_ID);
         
         if(cookie == null) {
            String value = UUID.randomUUID().toString();
            resp.setCookie(SESSION_ID, value);
         }
         resp.setDate(DATE, time);
         resp.setValue(SERVER, "Apache/2.2.14");
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