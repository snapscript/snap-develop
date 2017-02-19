/*
 * CompletionResource.java December 2016
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
import java.util.Map;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.develop.configuration.ConfigurationClassLoader;
import org.snapscript.develop.http.project.Project;
import org.snapscript.develop.http.project.ProjectBuilder;
import org.snapscript.develop.http.resource.Resource;

import com.google.gson.Gson;

// /complete/<project>
public class CompletionResource implements Resource {

   private final ConfigurationClassLoader loader;
   private final CompletionProcessor completer;
   private final ProjectBuilder builder;
   private final Gson gson;
   
   public CompletionResource(ProjectBuilder builder, ConfigurationClassLoader loader, ProcessLogger logger) {
      this.completer = new CompletionProcessor(loader, logger);
      this.gson = new Gson();
      this.builder = builder;
      this.loader = loader;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      CompletionResponse result = new CompletionResponse();
      PrintStream out = response.getPrintStream();
      String content = request.getContent();
      Path path = request.getPath();
      Thread thread = Thread.currentThread();
      ClassLoader classLoader = loader.getClassLoader();
      thread.setContextClassLoader(classLoader);
      Project project = builder.createProject(path);
      CompletionRequest context = gson.fromJson(content, CompletionRequest.class);
      Map<String, String> tokens = completer.createTokens(context, project);
      result.setTokens(tokens);
      String text = gson.toJson(result);
      response.setContentType("application/json");
      out.println(text);
      out.close();
   }
}
