package org.snapscript.studio.service.agent;

import java.net.URI;

import org.snapscript.studio.agent.DebugAgent;
import org.snapscript.studio.agent.RunMode;

public class DebugAgentRunner {

   public static void main(String[] list) throws Exception {
      String system = System.getProperty("os.name");
      URI resources = URI.create(list[0]);
      String process = list[1];
      String level = list[2];
      String type = list[3];
      RunMode mode = RunMode.resolveMode(type);
      
      start(mode, resources, system, process, level);
   }
   
   public static void start(RunMode mode, URI resources, String system, String process, String level) throws Exception {
      DebugAgent agent = new DebugAgent(mode, resources, system, process, level);
      agent.start();
   }
}