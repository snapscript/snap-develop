package org.snapscript.studio.common.resource;

import org.simpleframework.http.socket.service.DirectRouter;
import org.simpleframework.http.socket.service.Service;
import org.springframework.stereotype.Component;

@Component
public class WebSocketRouter extends DirectRouter {

   public WebSocketRouter(Service service) {
      super(service);
   }

}
