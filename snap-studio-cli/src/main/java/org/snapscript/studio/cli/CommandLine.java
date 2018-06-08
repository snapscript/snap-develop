package org.snapscript.studio.cli;

import java.io.File;
import java.util.List;

import org.snapscript.common.store.Store;
import org.snapscript.core.module.Path;
import org.snapscript.core.scope.Model;
import org.snapscript.studio.cli.store.StoreBuilder;

public class CommandLine {

   private final StoreBuilder builder;
   private final String evaluation;
   private final List<File> classpath;
   private final Path script;
   private final Model model;
   private final boolean debug;
   
   public CommandLine(Model model, String url, File root, List<File> classpath, Path script, String evaluation, boolean debug) {
      this.builder = new StoreBuilder(url, root, script, debug);
      this.evaluation = evaluation;
      this.classpath = classpath;
      this.script = script;
      this.model = model;
      this.debug = debug;
   }
   
   public void validate() {
      if(script != null) {
         String resource = script.getPath();
         Store store = builder.create();
         
         store.getInputStream(resource);
      }
   }
   
   public boolean isDebug() {
      return debug;
   }
   
   public Model getModel() {
      return model;
   }
   
   public Store getStore() {
      return builder.create();
   }
   
   public List<File> getClasspath() {
      return classpath;
   }
   
   public Path getScript() {
      return script;
   }

   public String getEvaluation() {
      return evaluation;
   }
}