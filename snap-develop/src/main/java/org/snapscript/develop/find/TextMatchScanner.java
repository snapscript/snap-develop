/*
 * TextMatchScanner.java December 2016
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.simpleframework.http.Path;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.develop.common.FilePatternMatcher;
import org.snapscript.develop.http.project.Project;
import org.snapscript.develop.http.project.ProjectBuilder;

public class TextMatchScanner {

   private final TextMatchFinder finder;
   private final ProjectBuilder builder;
   private final ProcessLogger logger;
   
   public TextMatchScanner(ProjectBuilder builder, ProcessLogger logger) {
      this.finder = new TextMatchFinder(logger);
      this.builder = builder;
      this.logger = logger;
   }
   
   public List<TextMatch> scanFiles(Path path, String expression) throws Exception {
      List<TextMatch> matches = new ArrayList<TextMatch>();
      Project project = builder.createProject(path);
      String name = project.getProjectName();
      File directory = project.getProjectPath();
      String root = directory.getCanonicalPath();
      Pattern pattern = Pattern.compile(".*.snap");
      List<File> list = FilePatternMatcher.scan(pattern, directory);
      int length = root.length();
      
      if(root.endsWith("/")) {
         root = root.substring(0, length -1);
      }
      for(File file : list) {
         String filePath = file.getCanonicalPath();
         String relativePath = filePath.replace(root, "");
         String resourcePath = relativePath.replace(File.separatorChar, '/');
         
         if(!resourcePath.startsWith("/")) {
            resourcePath = "/" + resourcePath;
         }
         List<TextMatch> match = finder.findText(file, name, resourcePath, expression);
         
         if(!match.isEmpty()) {
            matches.addAll(match);
         }
      }
      return matches;
   }

}
