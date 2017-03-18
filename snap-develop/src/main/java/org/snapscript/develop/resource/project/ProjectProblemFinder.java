/*
 * ProjectProblemFinder.java December 2016
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
import java.util.Set;

import org.simpleframework.http.Path;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.common.ThreadPool;
import org.snapscript.develop.common.FileAction;
import org.snapscript.develop.common.FileProcessor;
import org.snapscript.develop.common.FileReader;
import org.snapscript.develop.common.Problem;
import org.snapscript.develop.common.ProblemFinder;

public class ProjectProblemFinder {

   private final FileProcessor<Problem> processor;
   private final FileAction<Problem> action;
   private final ProjectBuilder builder;
   private final ProcessLogger logger;
   
   public ProjectProblemFinder(ProjectBuilder builder, ProcessLogger logger, ThreadPool pool) {
      this.action = new CompileAction(builder, logger);
      this.processor = new FileProcessor<Problem>(action, pool);
      this.builder = builder;
      this.logger = logger;
   }
   
   public Set<Problem> compileProject(Path path) throws Exception {
      Project project = builder.createProject(path);
      String name = project.getProjectName();
      File directory = project.getProjectPath();
      String root = directory.getCanonicalPath();
      int length = root.length();
      
      if(root.endsWith("/")) {
         root = root.substring(0, length -1);
      }
      long start = System.currentTimeMillis();
      
      try {
         return processor.process(name, root + "/**.snap"); // build all resources
      } finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.debug("Took " + duration + " ms to compile project " + name);
      }
   }
   
   private static class CompileAction implements FileAction<Problem> {
   
      private final ProjectBuilder builder;
      private final ProblemFinder finder;
      private final ProcessLogger logger;
      
      public CompileAction(ProjectBuilder builder, ProcessLogger logger) {
         this.finder = new ProblemFinder();
         this.builder = builder;
         this.logger = logger;
      }
      
      @Override
      public Problem execute(String reference, File file) throws Exception {
         Project project = builder.getProject(reference);
         String name = project.getProjectName();
         File root = project.getProjectPath();
         String rootPath = root.getCanonicalPath();
         String filePath = file.getCanonicalPath();
         String relativePath = filePath.replace(rootPath, "");
         String resourcePath = relativePath.replace(File.separatorChar, '/');
         
         if(!resourcePath.startsWith("/")) {
            resourcePath = "/" + resourcePath;
         }
         String source = FileReader.readText(file);
         
         if(logger.isTrace()) {
            logger.trace("Compiling " + resourcePath + " in project " + reference);
         }
         return finder.parse(name, resourcePath, source);
      }
   }
}
