/*
 * ConnectListener.java December 2016
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;

import org.simpleframework.http.Path;
import org.snapscript.develop.command.CommandListener;
import org.snapscript.develop.command.ExecuteCommand;
import org.snapscript.develop.http.project.Project;
import org.snapscript.develop.http.project.ProjectBuilder;

public class ConnectListener {

   private final ProjectBuilder builder;
   
   public ConnectListener(ProjectBuilder builder) {
      this.builder = builder;
   }
   
   public void connect(CommandListener listener, Path path) {
      String script = CommandLineArgument.SCRIPT.getValue();
      
      if(script != null) {
         try {
            Project project = builder.createProject(path);
            File projectPath = project.getProjectPath();
            String projectName = project.getProjectName();
            File file = new File(projectPath, "/" + script);
            FileInputStream input = new FileInputStream(file);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[1024];
            int count = 0;
            
            while((count = input.read(chunk))!=-1) {
               buffer.write(chunk, 0, count);
            }
            input.close();
            buffer.close();
            
            String source = buffer.toString("UTF-8");
            String system = System.getProperty("os.name");
            ExecuteCommand command = new ExecuteCommand(projectName, system, script, source, Collections.EMPTY_MAP);
            
            listener.onExecute(command);
         } catch(Exception e) {
            e.printStackTrace();
         }
      }
   }
}
