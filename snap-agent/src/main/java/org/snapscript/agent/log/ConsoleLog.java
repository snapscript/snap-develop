/*
 * ConsoleLog.java December 2016
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

package org.snapscript.agent.log;

import java.io.PrintStream;

public class ConsoleLog implements ProcessLog{
   
   private final PrintStream stream;
   
   public ConsoleLog() {
      this.stream = System.out;
   }

   @Override
   public void log(Object text) {
      stream.println(text);
   }

   @Override
   public void log(Object text, Throwable cause) {
      stream.print(text);
      
      if(cause != null) {
         stream.print(": ");
         cause.printStackTrace(stream);
      }else {
         stream.println();
      }
   }

}
