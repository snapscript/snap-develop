/*
 * CommandLineArgument.java December 2016
 *
 * Copyright (C) 2016, Niall Gallagher <niallg@users.sf.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

package org.snapscript.develop;

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
