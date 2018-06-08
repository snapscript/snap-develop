package org.snapscript.studio.cli;

import java.io.File;

import org.snapscript.common.store.Store;
import org.snapscript.core.module.Path;
import org.snapscript.core.scope.Model;
import org.snapscript.studio.cli.store.StoreBuilder;

public class CommandLine {

   private final StoreBuilder builder;
   private final String evaluation;
   private final File classpath;
   private final Path script;
   private final Model model;
   
   public CommandLine(Model model, String url, File root, File classpath, Path script, String evaluation, boolean debug) {
      this.builder = new StoreBuilder(url, root, script, debug);
      this.evaluation = evaluation;
      this.classpath = classpath;
      this.script = script;
      this.model = model;
   }
   
   public void validate() {
      if(!classpath.exists()) {
         CommandLineUsage.usage("Could not find classpath " + classpath);
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