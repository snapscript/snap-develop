package org.snapscript.studio.common.resource;

import static org.simpleframework.http.Protocol.DATE;
import static org.simpleframework.http.Protocol.SERVER;

import java.util.UUID;

import org.simpleframework.http.Cookie;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snapscript.core.Bug;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WebContainer implements Container {

   private static final Logger LOG = LoggerFactory.getLogger(WebContainer.class);

   private final WebSocketContainer container;
   private final String session;

   @Bug("this is rubbish")
   public WebContainer(WebSocketContainer container, @Value("${session.name:SESSID}") String session) {
      this.container = container;
      this.session = session;
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