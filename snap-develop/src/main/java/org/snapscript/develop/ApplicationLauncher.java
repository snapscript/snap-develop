/*
 * ApplicationLauncher.java December 2016
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

import java.util.Map;
import java.util.Set;

import org.snapscript.develop.http.project.ProjectMode;
import org.snapscript.service.ScriptService;

//--mode=develop --directory=work --port=4457 --agent-pool=4 --agent-port=4456 --log-level=DEBUG
public class ApplicationLauncher {
   
   private static final String PROJECT_MODE = "project-mode";
   private static final String MODE_ARGUMENT = "mode";
   private static final String DEFAULT_MODE = "develop";
   private static final String RUN_MODE = "run";
   
   public static void main(String[] list) throws Exception {     
      Map<String, String> commands = CommandLineParser.parse(list);
      Set<String> names = commands.keySet();
      String mode = DEFAULT_MODE;
      
      for(String name : names) {
         String value= commands.get(name);
         
         System.out.println("--" + name + "=" + value);
         System.setProperty(name, value); // make available to configuration
      }
      System.setProperty("java.awt.headless", "true");

      if(commands.containsKey(MODE_ARGUMENT)) { // is there a mode setting
         mode = commands.get(MODE_ARGUMENT);
      }
      if(!mode.equals(RUN_MODE)) {
         ApplicationContext service = new ApplicationContext("/context/" + mode + ".xml");
         
         if(!mode.equals(DEFAULT_MODE)) {
            System.setProperty(PROJECT_MODE, ProjectMode.SINGLE_MODE);
         } else {
            System.setProperty(PROJECT_MODE, ProjectMode.MULTIPLE_MODE);
         }
         service.start();
      } else {
         ScriptService.main(list);
      }
   }
}
