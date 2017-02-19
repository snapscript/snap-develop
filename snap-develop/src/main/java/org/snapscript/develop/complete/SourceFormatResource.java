/*
 * SourceFormatResource.java December 2016
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

package org.snapscript.develop.complete;

import java.io.PrintStream;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.develop.http.project.Project;
import org.snapscript.develop.http.project.ProjectBuilder;
import org.snapscript.develop.http.resource.Resource;

// /format/<project>
public class SourceFormatResource implements Resource {
   
   private static final int DEFAULT_INDENT = 3;

   private final SourceFormatter formatter;
   private final ProjectBuilder builder;
   
   public SourceFormatResource(ProjectBuilder builder) {
      this.formatter = new SourceFormatter();
      this.builder = builder;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      PrintStream out = response.getPrintStream();
      String content = request.getContent();
      Path path = request.getPath();
      String token = request.getParameter("indent");
      Integer indent = token == null ? DEFAULT_INDENT : Integer.parseInt(token);
      Project project = builder.createProject(path);
      String result = formatter.format(project, content, indent);
      response.setContentType("text/plain");
      out.println(result);
      out.close();
   }
}
