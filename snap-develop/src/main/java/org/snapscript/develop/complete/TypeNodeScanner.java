/*
 * TypeNodeScanner.java December 2016
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.simpleframework.http.Path;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.common.ThreadPool;
import org.snapscript.develop.common.FileAction;
import org.snapscript.develop.common.FileProcessor;
import org.snapscript.develop.common.FileReader;
import org.snapscript.develop.common.TypeNode;
import org.snapscript.develop.common.TypeNodeFinder;
import org.snapscript.develop.configuration.ConfigurationClassLoader;
import org.snapscript.develop.resource.project.Project;
import org.snapscript.develop.resource.project.ProjectBuilder;

public class TypeNodeScanner {

   private final FileProcessor<Map<String, TypeNode>> processor;
   private final FileAction<Map<String, TypeNode>> action;
   private final ProjectBuilder builder;
   private final ProcessLogger logger;
   
   public TypeNodeScanner(ProjectBuilder builder, ConfigurationClassLoader loader, ProcessLogger logger, ThreadPool pool) {
      this.action = new CompileAction(builder, loader, logger);
      this.processor = new FileProcessor<Map<String, TypeNode>>(action, pool);
      this.builder = builder;
      this.logger = logger;
   }
   
   public Map<String, TypeNodeReference> findTypes(Path path, String expression) throws Exception {
      Project project = builder.createProject(path);
      String name = project.getProjectName();
      File directory = project.getProjectPath();
      String root = directory.getCanonicalPath();
      long start = System.currentTimeMillis();
      int length = root.length();
      
      if(root.endsWith("/")) {
         root = root.substring(0, length -1);
      }
      Map<String, TypeNodeReference> typeNodes = new HashMap<String, TypeNodeReference>();
      
      try {
         Set<Map<String, TypeNode>> resourceTypes = processor.process(name, root + "/**.snap"); // build all resources
      
         for(Map<String, TypeNode> types : resourceTypes) {
            Set<String> typeNames = types.keySet();
            
            for(String typeName : typeNames) {
               TypeNode typeNode = types.get(typeName);
               String typePath = typeNode.getResource();
               
               if(typeName.matches(expression)) {
                  TypeNodeReference reference = null;
                  
                  if(typeNode.isModule()) {
                     reference = new TypeNodeReference(typeName, typePath, TypeNodeReference.MODULE);
                  } else {
                     reference = new TypeNodeReference(typeName, typePath, TypeNodeReference.CLASS);
                  }
                  typeNodes.put(typeName +":" + typePath, reference);
               }
            }
         }
         return typeNodes;
      } finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         if(logger.isDebug()) {
            logger.debug("Took " + duration + " ms to compile project " + name);
         }
      }
   }
   
   private static class CompileAction implements FileAction<Map<String, TypeNode>> {
   
      private final ProjectBuilder builder;
      private final TypeNodeFinder finder;
      private final ProcessLogger logger;
      
      public CompileAction(ProjectBuilder builder, ConfigurationClassLoader loader, ProcessLogger logger) {
         this.finder = new TypeNodeFinder(loader, logger);
         this.builder = builder;
         this.logger = logger;
      }
      
      @Override
      public Map<String, TypeNode> execute(String reference, File file) throws Exception {
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
         return finder.parse(root, name, resourcePath, source);
      }
   }
}
