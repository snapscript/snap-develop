package org.snapscript.agent.debug;

import org.snapscript.core.Scope;
import org.snapscript.core.trace.Trace;
import org.snapscript.core.trace.TraceListener;

public class TraceAdapter implements TraceListener {
   public void before(Scope scope, Trace trace) {}
   public void error(Scope scope, Trace trace, Exception cause) {}
   public void after(Scope scope, Trace trace) {}

}
