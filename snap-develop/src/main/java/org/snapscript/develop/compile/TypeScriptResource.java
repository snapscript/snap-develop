/*
 * TypeScriptResource.java December 2016
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

package org.snapscript.develop.compile;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.develop.resource.Resource;
import org.snapscript.develop.resource.ResourceMatcher;

public class TypeScriptResource implements Resource {

   private final TypeScriptCompiler compiler;
   private final ResourceMatcher matcher;
   private final List<File> outputDirs;
   private final List<String> sourceFiles;
   private final File typescriptDir;
   
   public TypeScriptResource(TypeScriptCompiler compiler, ResourceMatcher matcher, File typescriptDir, List<File> outputDirs, List<String> sourceFiles) {
      this.compiler = compiler;
      this.typescriptDir = typescriptDir;
      this.outputDirs = outputDirs;
      this.sourceFiles = sourceFiles;
      this.matcher = matcher;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      if(typescriptDir.exists()) {
         for(File outputDir : outputDirs) {
            compiler.compile(typescriptDir, outputDir, sourceFiles);
         }
      }
      Resource resource = matcher.match(request, response);
      
      if(resource == null) {
         throw new IOException("Could not match " + request);
      }
      resource.handle(request, response);
   }

}
