/*
 * ProcessServer.java December 2016
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

import java.net.InetSocketAddress;

import org.snapscript.develop.browser.BrowserLauncher;
import org.snapscript.develop.resource.WebServer;

public class ProcessServer {

   private final BrowserLauncher launcher;
   private final ProcessManager engine;
   private final WebServer server;
   
   public ProcessServer(ProcessManager engine, BrowserLauncher launcher, WebServer server) {
      this.launcher = launcher;
      this.engine = engine;
      this.server = server;
   }
   
   public void start() {
      try {
         InetSocketAddress address = server.start();
         int port = address.getPort();
         String host = address.getHostName();
         String project = String.format("http://%s:%s/", host, port);
         String script = CommandLineArgument.SCRIPT.getValue();
            
         if(script != null) {
            engine.launch(); // start a new process
         }
         System.err.println(project);
         launcher.launch(host, port);
         engine.start(host, port);
      } catch(Exception e) {
         e.printStackTrace();
      }
   }
}
