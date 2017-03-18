/*
 * ProjectTreeResource.java December 2016
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

package org.snapscript.develop.resource.project;

import java.io.File;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.simpleframework.http.Cookie;
import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.develop.resource.Resource;
import org.snapscript.develop.resource.display.DisplayModelResolver;
import org.snapscript.develop.resource.tree.TreeBuilder;
import org.snapscript.develop.resource.tree.TreeContext;
import org.snapscript.develop.resource.tree.TreeContextManager;

public class ProjectTreeResource implements Resource {
   
   private final TreeContextManager contextManager;
   private final AtomicInteger sessionCounter;
   private final ProjectBuilder projectBuilder;
   private final TreeBuilder treeBuilder;
   private final String session;
   
   public ProjectTreeResource(ProjectBuilder projectBuilder, TreeContextManager contextManager, DisplayModelResolver modelResolver, String session) {
      this.treeBuilder = new TreeBuilder(modelResolver);
      this.sessionCounter = new AtomicInteger();
      this.contextManager = contextManager;
      this.projectBuilder = projectBuilder;
      this.session = session;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      String name = request.getParameter("id");
      String expand = request.getParameter("expand");
      String folders = request.getParameter("folders");
      String depth = request.getParameter("depth");
      Path path = request.getPath(); // /tree/<project-name>
      String[] segments = path.getSegments();
      File treePath = projectBuilder.getRoot();
      boolean foldersOnly = false;
      int folderDepth = Integer.MAX_VALUE;
      
      if(segments.length > 1) {
         Project project = projectBuilder.createProject(path);
         treePath = project.getProjectPath();
      }
      if(depth != null) {
         folderDepth = Integer.parseInt(depth);
      }
      if(folders != null) {
         foldersOnly = Boolean.parseBoolean(folders);
      }
      int count = sessionCounter.getAndIncrement();
      Cookie cookie = request.getCookie(session);
      String value = String.valueOf(count);
      
      if(cookie != null) {
         value = cookie.getValue();
      } else {
         response.setCookie(session, value);
      }
      String projectName = treePath.getName();
      TreeContext context = contextManager.getContext(treePath, projectName, value);

      context.folderExpand(expand);
      String result = treeBuilder.createTree(context, name, foldersOnly, folderDepth);
      PrintStream out = response.getPrintStream();
      response.setContentType("text/html");
      out.println(result);
      out.close();
   }
}
