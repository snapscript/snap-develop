package org.snapscript.studio.agent.cli;

import java.util.regex.Pattern;

public interface CommandOption {
   String getCode();
   String getName();
   String getDescription();
   String getDefault();
   Pattern getPattern();
   Class getType();
}
