package org.snapscript.studio.agent.cli;

public interface CommandOption {
   String getCode();
   String getName();
   String getDescription();
   String getDefault();
   Class getType();
}
