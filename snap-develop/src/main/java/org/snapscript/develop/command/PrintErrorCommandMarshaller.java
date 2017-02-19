/*
 * PrintErrorCommandMarshaller.java December 2016
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

public class PrintErrorCommandMarshaller implements CommandMarshaller<PrintErrorCommand>{

   @Override
   public PrintErrorCommand toCommand(String value) {
      int offset = value.indexOf(':');
      String message = value.substring(offset + 1);
      int next = message.indexOf(':');
      String process = message.substring(0, next);
      String text = message.substring(next + 1);
      
      return new PrintErrorCommand(process, text);
   }

   @Override
   public String fromCommand(PrintErrorCommand command) {
      String process = command.getProcess();
      String text = command.getText();
 
      return CommandType.PRINT_ERROR + ":" + process + ":" + text;
   }

}
