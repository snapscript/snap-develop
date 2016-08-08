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
