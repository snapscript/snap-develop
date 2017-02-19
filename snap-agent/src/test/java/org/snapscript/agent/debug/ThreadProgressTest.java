package org.snapscript.agent.debug;

import junit.framework.TestCase;

import org.snapscript.core.Path;
import org.snapscript.core.trace.Trace;
import org.snapscript.core.trace.TraceType;

public class ThreadProgressTest extends TestCase {
   
   public void testThreadProgress() throws Exception {
      BreakpointMatcher matcher = new BreakpointMatcher();
      ThreadProgress progress = new ThreadProgress(matcher);
      
//      progress.beforeInstruction(TraceType.INVOKE);
//      progress.isSuspendBefore(createNormalTrace("x", 1));
   }

   private static Trace createNormalTrace(String resource, int line) {
      Path path = new Path(resource);
      return new Trace(TraceType.NORMAL, null, path, line);
   }
}
