/*
 * CommandReader.java December 2016
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

package org.snapscript.develop.command;

import java.util.HashMap;
import java.util.Map;

public class CommandReader {

   private final Map<String, CommandMarshaller> marshallers;

   public CommandReader() {
      this.marshallers = new HashMap<String, CommandMarshaller>();
   }
   
   public Command read(String text) throws Exception {
      if(marshallers.isEmpty()) {
         CommandType[] commands = CommandType.values();
         
         for(CommandType command : commands) {
            CommandMarshaller marshaller = command.marshaller.newInstance();
            String name = command.name();
            marshallers.put(name, marshaller);
         }
      }
      int offset = text.indexOf(':');
      String key = text;
      
      if(offset != -1) {
         key = text.substring(0, offset);
      }
      CommandMarshaller marshaller = marshallers.get(key);
      
      return marshaller.toCommand(text);
   }
}
