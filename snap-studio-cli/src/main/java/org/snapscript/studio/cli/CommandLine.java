package org.snapscript.studio.cli;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.snapscript.common.store.Store;
import org.snapscript.core.module.Path;
import org.snapscript.core.scope.Model;
import org.snapscript.studio.cli.store.ProcessStore;
import org.snapscript.studio.cli.store.ProcessStoreBuilder;

public class CommandLine {   
   
   private static final String PROCESS_PREFIX = "process";

   private final ProcessStoreBuilder builder;
   private final String evaluation;
   private final List<File> classpath;
   private final Integer port;
   private final Path script;
   private final Model model;
   private final boolean debug;
   private final boolean check;
   
   public CommandLine(Model model, String url, File root, List<File> classpath, Path script, String evaluation, Integer port, boolean debug, boolean check) {
      this.builder = new ProcessStoreBuilder(url, root, script, debug);
      this.evaluation = evaluation;
      this.classpath = classpath;
      this.script = script;
      this.model = model;
      this.debug = debug;
      this.check = check;
      this.port = port;
   }
   
   public void validate() {
      if(script != null) {
         String resource = script.getPath();
         Store store = builder.create();
         
         store.getInputStream(resource);
      }
   }
   
   public String getProcess() {
      return String.format("%s-%s", PROCESS_PREFIX, getProcessId());
   }
   
   public String getSystem() {
      return System.getProperty("os.name", "unknown");
   }
   
   public boolean isDebug() {
      return debug;
   }
   
   public boolean isCheck() {
      return check;
   }
   
   public Integer getPort() {
      return port;
   }
   
   public Model getModel() {
      return model;
   }
   
   public ProcessStore getStore() {
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
   
   private static String getProcessId() {
      try {
         return java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
      }catch(Exception e) {
         return UUID.randomUUID().toString();
      }
   }
}