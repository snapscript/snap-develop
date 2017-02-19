/*
 * ProjectBuilder.java December 2016
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.simpleframework.http.Path;
import org.snapscript.develop.Workspace;

public class ProjectBuilder {
   
   private static final String DEFAULT_PROJECT = "default";

   private final Map<String, Project> projects;
   private final Workspace workspace;
   private final ProjectMode mode;
   private final Project single;
   
   public ProjectBuilder(Workspace workspace, String mode){
      this.projects = new ConcurrentHashMap<String, Project>();
      this.single = new Project(workspace, ".", DEFAULT_PROJECT);
      this.mode = new ProjectMode(mode);
      this.workspace = workspace;
   }
   
   public File getRoot() {
      return workspace.create();
   }
   
   public Project getProject(String name){ 
      return projects.get(name);
   }
   
   public Project createProject(Path path){ // /project/<project-name>/ || /project/default
      if(mode.isMultipleMode()) { // multiple project support
         String projectPrefix = path.getPath(1, 1); // /<project-name>
         String projectName = projectPrefix.substring(1); // <project-name>
         Project project = projects.get(projectName);
         
         if(project == null) {
            project = new Project(workspace, projectName, projectName);
            projects.put(projectName, project);
         }
         File file = project.getProjectPath();
         
         if(!file.exists()) {
            file.mkdirs();
            createDefaultProject(file);
         }
         return project;
      }
      return single;
   }
   
   private void createDefaultProject(File file) {
      try {
         File directory = file.getCanonicalFile();
         
         if(!directory.exists() && !directory.mkdirs()) {
            throw new IllegalStateException("Could not build project directory " + directory);
         }
         File ignore = new File(directory, ".gitignore");
         OutputStream stream = new FileOutputStream(ignore);
         PrintStream print = new PrintStream(stream);
         print.println("/temp/");
         print.println("/.temp/");   
         print.println("/.backup/");     
         print.close();
      }catch(Exception e) {
         e.printStackTrace();
      }
   }
}
