/*
 * ProcessRunner.java December 2016
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

import java.net.URI;

import org.snapscript.agent.ProcessAgent;
import org.snapscript.agent.ProcessMode;

public class ProcessRunner {

   public static void main(String[] list) throws Exception {
      String system = System.getProperty("os.name");
      URI resources = URI.create(list[0]);
      String process = list[1];
      String level = list[2];
      String type = list[3];
      ProcessMode mode = ProcessMode.resolveMode(type);
      
      start(mode, resources, system, process, level);
   }
   
   public static void start(ProcessMode mode, URI resources, String system, String process, String level) throws Exception {
      ProcessAgent agent = new ProcessAgent(mode, resources, system, process, level);
      agent.start();
   }
}
