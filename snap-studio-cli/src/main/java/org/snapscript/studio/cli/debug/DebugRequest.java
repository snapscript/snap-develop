package org.snapscript.studio.cli.debug;

import java.net.URI;

public class DebugRequest {

   private final String project;
   private final String host;
   private final int port;
   
   public DebugRequest(String project, String host, int port) {
      this.project = project;
      this.host = host;
      this.port = port;
   }
   
   public URI getTarget(){
      try {
         return new URI("http://" + host +  ":" + port);
      } catch(Exception e) {
         throw new IllegalStateException("Could not build connection", e);
      }
   }
   
   public String getProject(){
      return project;
   }
}
