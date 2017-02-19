/*
 * ProjectDisplayResource.java December 2016
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

package org.snapscript.develop.http.project;

import java.io.File;
import java.io.PrintStream;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.xml.core.Persister;
import org.snapscript.develop.http.resource.Resource;

import com.google.gson.Gson;

// /theme/<project>
public class ProjectDisplayResource implements Resource {
   
   private static final String DEFAULT_FONT = "Consolas";
   private static final String DEFAULT_LOGO = "/img/logo_grey_shade.png";
   private static final int DEFAULT_SIZE = 14;
   private static final int DEFAULT_CAPACITY = 50000;
   
   private final ProjectDisplay display;
   private final ProjectBuilder builder;
   private final Persister persister;
   private final String theme;
   private final Gson gson;
   
   public ProjectDisplayResource(ProjectBuilder builder, String theme) {
      this.display = new ProjectDisplay(null, DEFAULT_LOGO, DEFAULT_FONT, DEFAULT_SIZE, DEFAULT_CAPACITY);
      this.persister = new Persister();
      this.gson = new Gson();
      this.builder = builder;
      this.theme = theme;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      Path path = request.getPath(); 
      Project project = builder.createProject(path);
      PrintStream out = response.getPrintStream();
      File root = project.getProjectPath();
      File file = new File(root, theme);
      
      if(!file.exists()) {
         root = root.getParentFile();
         file = new File(root, theme);
      }
      if(file.exists()) {
         ProjectDisplay display = persister.read(ProjectDisplay.class, file);
         String text = gson.toJson(display);
         response.setStatus(Status.OK);
         response.setContentType("application/json");
         out.println(text);
         out.close();
      } else {
         String text = gson.toJson(display);
         response.setStatus(Status.OK);
         response.setContentType("application/json");
         out.println(text);
         out.close();
         // save default display
         persister.write(display, file);
      }
   }

}
