package org.snapscript.studio.service.agent.remote;

import java.net.Socket;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.snapscript.studio.cli.debug.AttachRequest;
import org.snapscript.studio.cli.debug.DebugRequestProducer;
import org.snapscript.studio.cli.debug.DetachRequest;
import org.snapscript.studio.project.config.ProcessConfiguration;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteDebugService {
   
   private final ProcessConfiguration configuration;
   private final DebugRequestProducer producer; 
   
   public RemoteDebugService(ProcessConfiguration configuration) {
      this.producer = new DebugRequestProducer();
      this.configuration = configuration;
   }
   
   @SneakyThrows
   public AttachRequest attach(String projectName, String remoteHost, int remotePort){
      String localHost = configuration.getHost();
      int localPort = configuration.getPort();
      
      try {
         AttachRequest request = new AttachRequest(projectName, localHost, localPort);
         Socket socket = new Socket(remoteHost, remotePort);
         
         producer.write(socket, request);  
         socket.close();
         return request;
      } catch(Exception e) {
         log.info("Could not attach", e);
      }  
      return null;
   }
   
   @SneakyThrows
   public DetachRequest detach(String projectName, String remoteHost, int remotePort){
      String localHost = configuration.getHost();
      int localPort = configuration.getPort();
      
      try {
         DetachRequest request = new DetachRequest(projectName, localHost, localPort);
         Socket socket = new Socket(remoteHost, remotePort);
         
         producer.write(socket, request);  
         socket.close();
         return request;
      } catch(Exception e) {
         log.info("Could not detach", e);
      }  
      return null;
   }
}
