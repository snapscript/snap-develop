package org.snapscript.studio.service.bridge;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;

import javax.net.ssl.SSLContext;
import javax.ws.rs.ProcessingException;

import org.glassfish.jersey.simple.SimpleContainer;
import org.glassfish.jersey.simple.SimpleServer;
import org.glassfish.jersey.simple.SimpleTraceAnalyzer;
import org.glassfish.jersey.simple.internal.LocalizationMessages;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerSocketProcessor;
import org.simpleframework.http.socket.service.Router;
import org.simpleframework.http.socket.service.RouterContainer;
import org.simpleframework.transport.SocketProcessor;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

public class JerseyServer {

   private static final int DEFAULT_HTTPS_PORT = 443;
   private static final int DEFAULT_HTTP_PORT = 80;
   
   public static SimpleServer create(URI address, SSLContext context, final SimpleContainer container, Router router) throws Exception {
      String scheme = address.getScheme();
      int defaultPort = DEFAULT_HTTP_PORT;

      if (context == null) {
         if (!scheme.equalsIgnoreCase("http")) {
            throw new IllegalArgumentException(LocalizationMessages.WRONG_SCHEME_WHEN_USING_HTTP());
         }
      } else {
         if (!scheme.equalsIgnoreCase("https")) {
            throw new IllegalArgumentException(LocalizationMessages.WRONG_SCHEME_WHEN_USING_HTTPS());
         }
         defaultPort = DEFAULT_HTTPS_PORT;
      }
      int port = address.getPort();

      if (port == -1) {
         port = defaultPort;
      }
      final InetSocketAddress listen = new InetSocketAddress(port);
      final Connection connection;
      try {
         final SimpleTraceAnalyzer analyzer = new SimpleTraceAnalyzer();
         final Container inner = router == null ? container : new RouterContainer(container, router, 5);
         final SocketProcessor server = new ContainerSocketProcessor(inner);
         connection = new SocketConnection(server, analyzer);

         final SocketAddress socketAddr = connection.connect(listen, context);
         container.getApplicationHandler().onStartup(container);

         return new SimpleServer() {

            @Override
            public void close() throws IOException {
               container.getApplicationHandler().onShutdown(container);
               analyzer.stop();
               connection.close();
            }

            @Override
            public int getPort() {
               return ((InetSocketAddress) socketAddr).getPort();
            }

            @Override
            public boolean isDebug() {
               return analyzer.isActive();
            }

            @Override
            public void setDebug(boolean enable) {
               if (enable) {
                  analyzer.start();
               } else {
                  analyzer.stop();
               }
            }
         };
      } catch (final IOException ex) {
         throw new ProcessingException(LocalizationMessages.ERROR_WHEN_CREATING_SERVER(), ex);
      }
   }
}
