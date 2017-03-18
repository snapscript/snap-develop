
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
      String type = list[3];
      ProcessMode mode = ProcessMode.resolveMode(type);
      
      start(mode, resources, system, process, level);
   }
   
   public static void start(ProcessMode mode, URI resources, String system, String process, String level) throws Exception {
      ProcessAgent agent = new ProcessAgent(mode, resources, system, process, level);
      agent.start();
   }
}
