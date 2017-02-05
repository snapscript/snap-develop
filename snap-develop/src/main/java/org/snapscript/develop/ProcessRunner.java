package org.snapscript.develop;

import static org.snapscript.agent.ProcessMode.ATTACHED;

import java.net.URI;

import org.snapscript.agent.ProcessAgent;

public class ProcessRunner {

   public static void main(String[] list) throws Exception {
      String system = System.getProperty("os.name");
      URI resources = URI.create(list[0]);
      String process = list[1];
      String level = list[2];
      int port = Integer.parseInt(list[3]);
      
      start(resources, system, process, level, port);
   }
   
   public static void start(URI resources, String system, String process, String level, int port) throws Exception {
      ProcessAgent agent = new ProcessAgent(resources, system, process, level, port);
      agent.start(ATTACHED);
   }
}
