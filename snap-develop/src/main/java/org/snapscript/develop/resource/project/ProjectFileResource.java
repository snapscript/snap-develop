/*
 * ProjectFileResource.java December 2016
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

import java.io.OutputStream;
import java.io.PrintStream;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.core.Reserved;
import org.snapscript.develop.resource.ContentTypeResolver;
import org.snapscript.develop.resource.Resource;

public class ProjectFileResource implements Resource {
   
   private final ContentTypeResolver resolver;
   private final ProjectFileCache cache;
   private final ProcessLogger logger;
   
   public ProjectFileResource(ProjectBuilder builder, ContentTypeResolver resolver, ProcessLogger logger){
      this.cache = new ProjectFileCache(builder);
      this.resolver = resolver;
      this.logger = logger;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      Path path = request.getPath(); 
      String projectPath = path.getPath(2); // /<project-name>/<project-path> or /default/blah.snap
      ProjectFile projectFile = cache.getFile(path);
      OutputStream stream = response.getOutputStream();
      String type = resolver.resolveType(projectPath);
      String method = request.getMethod();
      
      response.setStatus(Status.OK);
      response.setContentType(type);
      
      if(logger.isTrace()) {
         logger.trace(method + ": " + path);
      }
      try {
         byte[] resource = projectFile.getByteArray();
         
         stream.write(resource);
         stream.close();
      }catch(Exception e) {
         PrintStream out = response.getPrintStream();
         response.setStatus(Status.NOT_FOUND);
         
         if(projectPath.endsWith(Reserved.SCRIPT_EXTENSION)){
            out.println("// No source found for " + projectPath);
         }
         out.close();
      }
   }
}
