
package org.snapscript.service;

import java.io.File;

import org.snapscript.core.Model;
import org.snapscript.core.store.FileStore;
import org.snapscript.core.store.Store;

public class CommandLine {

   private final FileStore store;
   private final File directory;
   private final File classpath;
   private final String script;
   private final String evaluation;
   private final Model model;
   
   public CommandLine(Model model, File directory, File classpath, String script, String evaluation) {
      this.store = new FileStore(directory);
      this.evaluation = evaluation;
      this.classpath = classpath;
      this.directory = directory;
      this.script = script;
      this.model = model;
   }
   
   public void validate() {
      if(!directory.exists()) {
         throw new IllegalArgumentException("Could not find work directory " + directory);
      }
      if(!classpath.exists()) {
         throw new IllegalArgumentException("Could not find classpath directory " + classpath);
      }
      if(!directory.isDirectory()) {
         throw new IllegalArgumentException("Work directory " + directory + " is not a directory");
      }
      if(script != null) {
         store.getInputStream(script);
      }
   }
   
   public Model getModel() {
      return model;
   }
   
   public Store getStore() {
      return store;
   }
   
   public File getClasspath() {
      return classpath;
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
