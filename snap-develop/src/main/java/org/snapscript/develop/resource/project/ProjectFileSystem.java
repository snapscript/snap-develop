/*
 * ProjectFileSystem.java December 2016
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.simpleframework.http.Path;

public class ProjectFileSystem {

   private final Project project;
   
   public ProjectFileSystem(Project project) {
      this.project = project;
   }
   
   public File getFile(Path path) {
      String projectPath = path.getPath(2); // /<project-name>/<project-path> or /default/blah.snap
      File sourcePath = project.getSourcePath();
      String realPath = projectPath.replace('/', File.separatorChar);
      return new File(sourcePath, realPath);
   }
   
   public void writeAsByteArray(String path, String resource) throws Exception {
      byte[] octets = resource.getBytes("UTF-8");
      writeAsByteArray(path, octets);
   }
   
   public void writeAsByteArray(String path, byte[] resource) throws Exception {
      File sourcePath = project.getSourcePath();
      String realPath = path.replace('/', File.separatorChar);
      File sourceFile = new File(sourcePath, realPath);
      FileOutputStream outputStream = new FileOutputStream(sourceFile);
      outputStream.write(resource);
      outputStream.close();
   }
   
   public String readAsString(String path) throws Exception {
      byte[] resource = readAsByteArray(path);
      return new String(resource, "UTF-8");
   }
   
   public byte[] readAsByteArray(String path) throws Exception {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      File sourcePath = project.getSourcePath();
      File rootPath = project.getProjectPath();
      String realPath = path.replace('/', File.separatorChar);
      File sourceFile = new File(sourcePath, realPath);
      File projectFile = new File(rootPath, realPath);
      InputStream inputStream = null;
      
      if(sourceFile.exists()) {
         inputStream = new FileInputStream(sourceFile);
      } else if(projectFile.exists()) {
         inputStream = new FileInputStream(projectFile);
      } else {
         inputStream = ProjectFileResource.class.getResourceAsStream(path);
      }
      byte[] chunk = new byte[1024];
      int count = 0;

      while((count = inputStream.read(chunk)) != -1) {
         buffer.write(chunk, 0, count);
      }
      inputStream.close();
      return buffer.toByteArray();
   }
   
   public ProjectFile readFile(Path path) throws Exception {
      String projectPath = path.getPath(2); // /<project-name>/<project-path> or /default/blah.snap
      return readFile(projectPath);
   }
   
   public ProjectFile readFile(String path) throws Exception {
      long time = System.currentTimeMillis();
      File sourcePath = project.getSourcePath();
      File rootPath = project.getProjectPath();
      String realPath = path.replace('/', File.separatorChar);
      File sourceFile = new File(sourcePath, realPath);
      File projectFile = new File(rootPath, realPath);
      
      if(sourceFile.exists()) {
         return new ProjectFile(this, path, sourceFile, time);
      } else if(projectFile.exists()) {
         return new ProjectFile(this, path, projectFile, time);
      }          
      return new ProjectFile(this, path, null, time);
   }
   
}
