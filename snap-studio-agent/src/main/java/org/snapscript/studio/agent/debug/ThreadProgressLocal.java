package org.snapscript.studio.agent.debug;

import org.snapscript.studio.agent.debug.BreakpointMatcher;
import org.snapscript.studio.agent.debug.ThreadProgress;

public class ThreadProgressLocal extends ThreadLocal<ThreadProgress> {
   
   private final BreakpointMatcher matcher;
   
   public ThreadProgressLocal(BreakpointMatcher matcher){
      this.matcher = matcher;
   }

   @Override
   protected ThreadProgress initialValue() {
      return new ThreadProgress(matcher);
   }
}