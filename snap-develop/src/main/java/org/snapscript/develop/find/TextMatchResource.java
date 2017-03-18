/*
 * TextMatchResource.java December 2016
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

package org.snapscript.develop.find;

import java.io.PrintStream;
import java.util.List;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.common.ThreadPool;
import org.snapscript.develop.resource.Resource;
import org.snapscript.develop.resource.project.ProjectBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TextMatchResource implements Resource {
   
   private final TextMatchScanner scanner;
   private final Gson gson;
   
   public TextMatchResource(ProjectBuilder builder, ProcessLogger logger, ThreadPool pool) {
      this.scanner = new TextMatchScanner(builder, logger, pool);
      this.gson = new GsonBuilder().setPrettyPrinting().create();
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      String pattern = request.getParameter("pattern");
      String query = request.getParameter("expression");
      Path path = request.getPath();
      PrintStream out = response.getPrintStream(8192);
      List<TextMatch> matches = scanner.scanFiles(path, pattern, query);
      String text = gson.toJson(matches);
      response.setContentType("application/json");
      out.println(text);
      out.close();
   }

}
