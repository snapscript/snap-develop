/*
 * RemoteProcessBuilder.java December 2016
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

package org.snapscript.develop.http.loader;

import static org.snapscript.develop.configuration.Configuration.JAR_FILE;
import static org.snapscript.develop.configuration.Configuration.TEMP_PATH;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.snapscript.develop.Workspace;
import org.snapscript.develop.configuration.Configuration;

public class RemoteProcessBuilder {
   
   public static final String LAUNCHER_CLASS = "/org/snapscript/develop/http/loader/RemoteProcessLauncher.class";
   public static final String LOADER_CLASS = "/org/snapscript/develop/http/loader/RemoteClassLoader.class";
   public static final String MAIN_CLASS = "org.snapscript.develop.http.loader.RemoteProcessLauncher";
   
   private final JarBuilder builder;
   private final Workspace workspace;
   
   public RemoteProcessBuilder(ClassResourceLoader loader, Workspace workspace) {
      this.builder = new JarBuilder(loader);
      this.workspace = workspace;
   }
   
   public void create() throws Exception {
      File directory = workspace.create(TEMP_PATH);
      
      if(!directory.exists()) {
         directory.mkdirs();
      }
      File file = new File(directory, JAR_FILE);
      
      if(file.exists()) {
         file.delete();
      }
      byte[] data = builder.createJar(MAIN_CLASS, LAUNCHER_CLASS, LOADER_CLASS);
      File parent = file.getParentFile();
      
      if(!parent.exists()) {
         parent.mkdirs();
      }
      OutputStream stream = new FileOutputStream(file);
      
      try {
         stream.write(data);
      } finally {
         stream.close();
      }
   }
}
