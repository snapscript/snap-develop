/*
 * CommandLine.java December 2016
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

package org.snapscript.service;

import java.io.File;

import org.snapscript.core.Model;
import org.snapscript.core.store.FileStore;
import org.snapscript.core.store.Store;

public class CommandLine {

   private final FileStore store;
   private final File directory;
   private final String script;
   private final String evaluation;
   private final Model model;
   
   public CommandLine(Model model, String path, String script, String evaluation) {
      this.directory = new File(path);
      this.store = new FileStore(directory);
      this.script = script;
      this.evaluation = evaluation;
      this.model = model;
   }
   
   public Model getModel() {
      return model;
   }
   
   public Store getStore() {
      return store;
   }

   public File getDirectory() {
      return directory;
   }

   public String getScript() {
      return script;
   }

   public String getEvaluation() {
      return evaluation;
   }
}
