package org.snapscript.studio.service.bridge;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/blah")
public class JerseyExample {

   @Inject
   public JerseyService service;
   
   @GET
   @Path("/foo")
   public String foo(){
      return service.getResponse();
   }
}
