package org.snapscript.develop.resource;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerSocketProcessor;
import org.simpleframework.transport.SocketProcessor;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

public class WebServer {

   private final SocketProcessor server;
   private final Connection connection;
   private final WebAddress address;
   
   public WebServer(Container container, int port) throws IOException {
      this.server = new ContainerSocketProcessor(container, 10);
      this.connection = new SocketConnection(server);
      this.address = new WebAddress(port);
   }

   public InetSocketAddress start() throws IOException {
	   try {
	      InetSocketAddress external = address.getExternalAddress();
	      InetSocketAddress internal = address.getLocalAddress();
         InetSocketAddress local = (InetSocketAddress)connection.connect(internal);
         String host = external.getHostString();
         int port = local.getPort();
         
         return new InetSocketAddress(host, port);
	   } catch (IOException ex) {
	      try {
	         SocketAddress local = address.getLocalAddress();
            return (InetSocketAddress)connection.connect(local);
	      }catch(Exception e) {
	         System.err.println("Failed to connect to: " + address);
		      throw ex;
	      }
	   }
   }

   public void stop() throws IOException {
      connection.close();
   }
}