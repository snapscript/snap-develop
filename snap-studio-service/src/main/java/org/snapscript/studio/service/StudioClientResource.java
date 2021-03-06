package org.snapscript.studio.service;

import lombok.AllArgsConstructor;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.snapscript.studio.common.resource.Resource;
import org.snapscript.studio.common.resource.ResourcePath;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@ResourcePath("/debugger")
public class StudioClientResource implements Resource {

   private final StudioClientLauncher launcher;

   @Override
   public void handle(Request request, Response response) throws Throwable {
      response.setStatus(Status.OK);
      launcher.debug();
   }
}
