package org.snapscript.develop.resource;

import java.net.InetSocketAddress;

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
         return new InetSocketAddress("localhost", port);
      }catch(Exception e){
         return new InetSocketAddress(port);
      }
   }
}