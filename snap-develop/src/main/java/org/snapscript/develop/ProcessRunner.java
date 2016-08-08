package org.snapscript.develop;

import java.net.URI;

import org.snapscript.agent.ProcessAgent;

public class ProcessRunner {

   public static void main(String[] list) throws Exception {
      URI resources = URI.create(list[0]);
      String process = list[1];
      int port = Integer.parseInt(list[2]);
      
      start(resources, process, port);
   }
   
   public static void start(URI resources, String process, int port) throws Exception {
      ProcessAgent agent = new ProcessAgent(resources, process, port);
      agent.start();
   }
}
