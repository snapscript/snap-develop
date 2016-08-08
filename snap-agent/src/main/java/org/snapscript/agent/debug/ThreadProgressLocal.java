package org.snapscript.agent.debug;

public class ThreadProgressLocal extends ThreadLocal<ThreadProgress> {

   @Override
   protected ThreadProgress initialValue() {
      return new ThreadProgress();
   }
}
