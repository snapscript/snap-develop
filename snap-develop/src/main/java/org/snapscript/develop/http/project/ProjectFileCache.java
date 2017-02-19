/*
 * ProjectFileCache.java December 2016
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.simpleframework.http.Path;

public class ProjectFileCache {

   private final Map<String, ProjectFile> cache;
   private final ProjectBuilder builder;
   
   public ProjectFileCache(ProjectBuilder builder) {
      this.cache = new ConcurrentHashMap<String, ProjectFile>();
      this.builder = builder;
   }
   
   public ProjectFile getFile(Path path) throws Exception {
      String projectPath = path.getPath(2); // /<project-name>/<project-path> or /default/blah.snap
      ProjectFile file = cache.get(projectPath);
      
      if(file == null || file.isStale()) {
         Project project = builder.createProject(path);
         ProjectFileSystem fileSystem = project.getFileSystem();
         
         file = fileSystem.readFile(projectPath);
         cache.put(projectPath, file);
      }
      return file;
   }
   
}
