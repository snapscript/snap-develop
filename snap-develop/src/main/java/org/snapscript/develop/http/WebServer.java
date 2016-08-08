package org.snapscript.develop.http;

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
   private final SocketAddress address;
   private final Connection connection;
   
   public WebServer(Container container, int port) throws IOException {
      this.server = new ContainerSocketProcessor(container, 2);
      this.connection = new SocketConnection(server);
      this.address = new InetSocketAddress(port);
   }

   public int start() throws IOException {
	   try {
		   InetSocketAddress local = (InetSocketAddress)connection.connect(address);
		   return local.getPort();
	   } catch (IOException ex) {
		   System.err.println("Failed to connect to: " + address);
		   throw ex;
	   }
   }

   public void stop() throws IOException {
      connection.close();
   }
}
