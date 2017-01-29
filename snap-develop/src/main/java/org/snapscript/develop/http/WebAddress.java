package org.snapscript.develop.http;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class WebAddress {

   private final int port;
   
   public WebAddress(int port) {
      this.port = port;
   }
   
   public InetSocketAddress getLocalAddress() {
      try {
         return new InetSocketAddress(port);
      }catch(Exception e){
         return new InetSocketAddress(port);
      }
   }
   
   public InetSocketAddress getExternalAddress() {
      try {
         String host = InetAddress.getLocalHost().getHostAddress();
         return new InetSocketAddress(host, port);
      }catch(Exception e){
         return new InetSocketAddress(port);
      }
   }
}
