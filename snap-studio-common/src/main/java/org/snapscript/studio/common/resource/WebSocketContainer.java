package org.snapscript.studio.common.resource;

import java.io.IOException;

import org.simpleframework.http.socket.service.Router;
import org.simpleframework.http.socket.service.RouterContainer;
import org.springframework.stereotype.Component;

@Component
public class WebSocketContainer extends RouterContainer {

   public WebSocketContainer(ResourceContainer container, Router router) throws IOException {
      super(container, router, 10);
   }

}
