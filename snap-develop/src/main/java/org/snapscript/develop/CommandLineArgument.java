package org.snapscript.develop;

import java.util.regex.Pattern;

public enum CommandLineArgument {
   AGENT_PORT("agent-port", "0", "Port for agent connections", "\\d+"),
   AGENT_POOL("agent-pool", "4", "Number of agents in pool", "\\d+"),
   PORT("port", "0", "Port for HTTP connections", "\\d+"),
   MODE("mode", "develop", "Mode to start on", "(develop|debug|run)"),
   DIRECTORY("directory", "work", "Directory used for sources", ".*"),
   VERBOSE("verbose", "false", "Verbosity of logging", "(true|false)"),
   SCRIPT("script", null, "Script to launch", ".*.snap");
   
   public final String description;
   public final Pattern pattern;
   public final String command;
   public final String value;
   
   private CommandLineArgument(String command, String value, String description, String pattern) {
      this.pattern = Pattern.compile(pattern);
      this.description = description;
      this.command = command;
      this.value = value;
   }
   
   public String getValue() {
      return System.getProperty(command);
   }
   
   public static Pattern getPattern(String command) {
      CommandLineArgument[] arguments = CommandLineArgument.values();
      
      for(CommandLineArgument argument : arguments) {
         String name = argument.command;
         
         if(name.equals(command)) {
            return argument.pattern;
         }
      }
      return null;
   }
}
