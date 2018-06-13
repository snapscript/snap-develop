package org.snapscript.studio.service.agent.remote;

import java.net.InetAddress;
import java.net.Socket;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.snapscript.studio.cli.debug.AttachRequest;
import org.snapscript.studio.cli.debug.AttachResponse;
import org.snapscript.studio.cli.debug.DebugRequestProducer;
import org.snapscript.studio.cli.debug.DetachRequest;
import org.snapscript.studio.cli.debug.DetachResponse;
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
   public AttachResponse attach(String projectName, String remoteHost, int remotePort){
      String localHost = InetAddress.getLocalHost().getCanonicalHostName();
      int localPort = configuration.getPort();
      
      try {
         AttachRequest request = new AttachRequest(projectName, localHost, localPort);
         Socket socket = new Socket(remoteHost, remotePort);
         
         try {
            return producer.attach(socket, request);
         }finally {
            socket.close();
         }
      } catch(Exception e) {
         throw new IllegalStateException("Could not attach to " + remoteHost + ":" + remotePort, e);
      }  
   }
   
   @SneakyThrows
   public DetachResponse detach(String projectName, String remoteHost, int remotePort){
      String localHost = InetAddress.getLocalHost().getCanonicalHostName();
      int localPort = configuration.getPort();
      
      try {
         DetachRequest request = new DetachRequest(projectName, localHost, localPort);
         Socket socket = new Socket(remoteHost, remotePort);
         
         try {
            return producer.detach(socket, request);
         }finally {
            socket.close();
         }
      } catch(Exception e) {
         throw new IllegalStateException("Could not detach to " + remoteHost + ":" + remotePort, e);
      }  
   }
}
