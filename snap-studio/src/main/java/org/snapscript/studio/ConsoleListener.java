package org.snapscript.studio;

public interface ConsoleListener {
   void onUpdate(String process, String line);
   void onUpdate(String process, String line, Throwable cause);
}