package org.snapscript.studio.core;

import java.util.regex.Pattern;

public enum CommandLineArgument {
   AGENT_PORT("agent-port", "0", "Port for agent connections", "\\d+"),
   AGENT_POOL("agent-pool", "4", "Number of agents in pool", "\\d+"),
   PORT("port", "0", "Port for HTTP connections", "\\d+"),
   MODE("mode", "develop", "Mode to start on", "(develop|debug|run)"),
   DIRECTORY("directory", "work", "Directory used for sources", ".*"),
   VERBOSE("log-level", "INFO", "Level of logging", "(TRACE|DEBUG|INFO)"),
   LOG("log-file", "log/snapd.log", "Log file to use", ".+"),
   SCRIPT("script", null, "Script to launch", ".*.snap"),
   SERVER_ONLY("server-only", "false", "Launch server only", "(true|false)"),
   BROWSER_ENGINE("browser-engine", "javafx", "Browser engine to use", "(javafx|cef)"),
   CLIENT_DEBUG("client-debug", "false", "Enable client debugger", "(true|false)"); // firebug
   
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