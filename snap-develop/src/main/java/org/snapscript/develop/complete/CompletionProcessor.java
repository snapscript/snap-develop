/*
 * CompletionProcessor.java December 2016
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

import java.io.File;
import java.util.Map;

import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.develop.configuration.ConfigurationClassLoader;
import org.snapscript.develop.resource.project.Project;

public class CompletionProcessor {
   
   private final CompletionCompiler builder;
   
   public CompletionProcessor(ConfigurationClassLoader loader, ProcessLogger logger) {
      this.builder = new CompletionCompiler(loader, logger);
   }

   public Map<String, String> createTokens(CompletionRequest request, Project project) {
      CompletionMatcher finder = builder.compile();
      Completion state = createState(request, project);
      
      return finder.findTokens(state);
   }
   
   private Completion createState(CompletionRequest request, Project project){
      String prefix = request.getPrefix();
      String source = request.getSource();
      String resource = request.getResource();
      String complete = request.getComplete();
      File root = project.getProjectPath();
      int line = request.getLine();
      
      return new Completion(root, source, resource, prefix, complete, line);
   }
}
