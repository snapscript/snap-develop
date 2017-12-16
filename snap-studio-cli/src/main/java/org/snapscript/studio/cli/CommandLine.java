package org.snapscript.studio.cli;

import java.io.File;

import org.snapscript.common.store.Store;
import org.snapscript.core.Model;
import org.snapscript.core.Path;

public class CommandLine {

   private final StoreBuilder builder;
   private final String evaluation;
   private final File classpath;
   private final Path script;
   private final Model model;
   
   public CommandLine(Model model, String root, File classpath, Path script, String evaluation) {
      this.builder = new StoreBuilder(root, script);
      this.evaluation = evaluation;
      this.classpath = classpath;
      this.script = script;
      this.model = model;
   }
   
   public void validate() {
      if(!classpath.exists()) {
         throw new IllegalArgumentException("Could not find classpath directory " + classpath);
      }
      if(script != null) {
         String resource = script.getPath();
         Store store = builder.create();
         
         store.getInputStream(resource);
      }
   }
   
   public Model getModel() {
      return model;
   }
   
   public Store getStore() {
      return builder.create();
   }
   
   public File getClasspath() {
      return classpath;
   }
   
   public Path getScript() {
      return script;
   }

   public String getEvaluation() {
      return evaluation;
   }
}