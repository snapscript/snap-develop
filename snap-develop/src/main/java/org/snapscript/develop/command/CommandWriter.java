/*
 * CommandWriter.java December 2016
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

public class CommandWriter {

   private final Map<Class, CommandMarshaller> marshallers;
   
   public CommandWriter() {
      this.marshallers = new HashMap<Class, CommandMarshaller>();
   }
   
   public String write(Command object) throws Exception {
      Class type = object.getClass();
      
      if(!marshallers.containsKey(type)) {
         CommandType[] commands = CommandType.values();
         
         for(CommandType command : commands) {
            CommandMarshaller marshaller = command.marshaller.newInstance();
            marshallers.put(command.command, marshaller);
         }
      }
      CommandMarshaller marshaller = marshallers.get(type);
      
      if(marshaller == null) {
         throw new IllegalStateException("Could not find marshaller for " + type);
      }
      return marshaller.fromCommand(object);
   }
}
