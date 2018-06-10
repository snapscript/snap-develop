package org.snapscript.studio.service.agent;

import java.net.URI;

import org.snapscript.studio.agent.DebugAgent;
import org.snapscript.studio.agent.DebugContext;
import org.snapscript.studio.agent.RemoteProjectStore;
import org.snapscript.studio.agent.RunMode;
import org.snapscript.studio.agent.TerminateListener;

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
      RemoteProjectStore store = new RemoteProjectStore(resources);
      Runnable listener = new TerminateListener(mode);
      DebugContext context = new DebugContext(mode, store, process, system);
      DebugAgent agent = new DebugAgent(context, level);
      
      agent.start(resources, listener);
   }
}