/*
 * WebServer.java December 2016
 *
 * Copyright (C) 2016, Niall Gallagher <niallg@users.sf.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

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
