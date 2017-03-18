
package org.snapscript.agent.log;

public interface ProcessLog {
   void log(Object text);
   void log(Object text, Throwable cause);
}
