package org.snapscript.develop;

import java.net.URI;

import org.snapscript.agent.ProcessAgent;
import org.snapscript.agent.ProcessMode;

public class ProcessRunner {

   public static void main(String[] list) throws Exception {
      String system = System.getProperty("os.name");
      URI resources = URI.create(list[0]);
      String process = list[1];
      String level = list[2];
      String mode = list[3];
      
      start(resources, system, process, level, mode);
   }
   
   public static void start(URI resources, String system, String process, String level, String type) throws Exception {
      ProcessAgent agent = new ProcessAgent(resources, system, process, level);
      ProcessMode mode = ProcessMode.resolveMode(type);
      agent.start(mode);
   }
}
