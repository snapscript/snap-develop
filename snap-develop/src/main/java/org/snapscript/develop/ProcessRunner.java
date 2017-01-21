package org.snapscript.develop;

import java.net.URI;

import org.snapscript.agent.ProcessAgent;

public class ProcessRunner {

   public static void main(String[] list) throws Exception {
      URI resources = URI.create(list[0]);
      String process = list[1];
      String level = list[2];
      int port = Integer.parseInt(list[3]);
      
      start(resources, process, level, port);
   }
   
   public static void start(URI resources, String process, String level, int port) throws Exception {
      ProcessAgent agent = new ProcessAgent(resources, process, level, port);
      agent.start();
   }
}
