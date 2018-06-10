package org.snapscript.studio.service.agent;

import java.io.PrintStream;
import java.net.Socket;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.snapscript.studio.cli.debug.DebugRequest;
import org.snapscript.studio.cli.debug.DebugRequestMarshaller;
import org.snapscript.studio.common.resource.Resource;
import org.snapscript.studio.common.resource.ResourcePath;
import org.snapscript.studio.project.config.ProcessConfiguration;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
@ResourcePath("/debug/.*")
public class DebugRequestResource implements Resource {
   
   private final DebugRequestMarshaller marshaller; // used when an event executes itself  
   private final ProcessConfiguration configuration;
   private final Gson gson;
   
   public DebugRequestResource(ProcessConfiguration configuration) {
      this.marshaller = new DebugRequestMarshaller();
      this.gson = new Gson();
      this.configuration = configuration;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      PrintStream stream = response.getPrintStream();
      Path path = request.getPath(); // /project/<project-name>/<project-path>
      String[] segments = path.getSegments();
      String projectName = segments[1];
      String remoteHost = segments[2];
      int remotePort = Integer.parseInt(segments[3]);
      String localHost = configuration.getHost();
      int localPort = configuration.getPort();
      
      try {
         DebugRequest message = new DebugRequest(projectName, localHost, localPort);
         Socket socket = new Socket(remoteHost, remotePort);
         String payload = gson.toJson(message);
         
         marshaller.writeRequest(socket, message);
         response.setStatus(Status.OK);
         response.setContentType("application/json");      
         stream.println(payload);     
      } catch(Exception e) {
         response.setStatus(Status.INTERNAL_SERVER_ERROR);
         response.setContentType("application/json");  
         stream.println("{}");
         e.printStackTrace();
      } finally {
         stream.close();
         response.close();  
      }
    
   }

}