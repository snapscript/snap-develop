package org.snapscript.develop;

public interface ProcessAgentController {
   boolean start(String process); // move from waiting to running, used by agent
   boolean stop(String process); // stop the process
   boolean ping(String process, long time); // ping the process
}
