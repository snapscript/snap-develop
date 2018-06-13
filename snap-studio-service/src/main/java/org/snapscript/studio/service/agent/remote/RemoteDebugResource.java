package org.snapscript.studio.service.agent.remote;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import lombok.AllArgsConstructor;

import org.snapscript.studio.cli.debug.AttachResponse;
import org.snapscript.studio.cli.debug.DetachResponse;

// /debug/<project>/<host>/<port>/attach
@Path("/debug")
@AllArgsConstructor(onConstructor=@__({@Inject}))
public class RemoteDebugResource {

   private final RemoteDebugService service;

   @GET
   @Path("/{project}/{host}/{port}/attach")
   @Produces("application/json")
   public Response attach(
         @PathParam("project") String project, 
         @PathParam("host") String host, 
         @PathParam("port") int port) throws Exception
   {
      AttachResponse response = service.attach(project, host, port);
      return Response.ok(response).build();
   }
   
   @GET
   @Path("/{project}/{host}/{port}/detach")
   @Produces("application/json")
   public Response detach(
         @PathParam("project") String project, 
         @PathParam("host") String host, 
         @PathParam("port") int port) throws Exception
   {
      DetachResponse response = service.detach(project, host, port);
      return Response.ok(response).build();
   }
}
