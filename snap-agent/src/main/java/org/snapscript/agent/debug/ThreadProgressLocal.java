
package org.snapscript.agent.debug;

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
