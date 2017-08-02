package org.snapscript.develop.compile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.snapscript.agent.log.ConsoleLog;
import org.snapscript.agent.log.ProcessLog;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.develop.ConsoleListener;
import org.snapscript.develop.ConsoleManager;

import com.google.javascript.jscomp.CompilationLevel;

public class TypeScriptCompiler {
   
   private static final String COMPRESS_SOURCE_FILE = "all.js";
   
   private final File compiler;
   private final File node;
   private final File root;
   
   public TypeScriptCompiler(String compiler, String node) {
      this.root = new File(".");
      this.node = new File(node);
      this.compiler = new File(root, compiler);
   }
   
   public synchronized void compile(File sourceDir, File outputDir, List<String> libraryFiles) throws Exception {
      File generatedFile = new File(outputDir, COMPRESS_SOURCE_FILE);
      
      if(!sourceDir.exists()) {
         throw new IOException("Source directory '" +sourceDir+"' does not exist");
      }
      if(!sourceDir.isDirectory()) {
         throw new IOException("Source directory '"+sourceDir+"' is actually a file");
      }
      if(!outputDir.exists()) {
         throw new IOException("Output directory '" +outputDir+"' does not exist");
      }
      if(!outputDir.isDirectory()) {
         throw new IOException("Output directory '"+outputDir+"' is actually a file");
      }
      if(node.exists() && compiler.exists()) {
         List<String> command = new ArrayList<String>();
         
         command.add(node.getCanonicalPath());
         command.add(compiler.getCanonicalPath()); 
         command.add("--module"); 
         command.add("AMD");
         
         if(outputDir.isDirectory()) {
            command.add("--outDir");
            command.add(outputDir.getCanonicalPath());
         }
         File[] sourceFiles = sourceDir.listFiles();
         File work = compiler.getParentFile();
         long outputTime = outputDir.lastModified();
         long sourceTime = 0;
         int outputCount = 0;
         
         for(File file : sourceFiles) {
            String name = file.getName();
            
            if(file.isFile() && name.endsWith(".ts")) {
               String path = file.getCanonicalPath();
               long lastModified = file.lastModified();
   
               if(sourceTime < lastModified) {
                  sourceTime = lastModified;
               }
               command.add(path);
            }
         }
         if(outputDir.isDirectory()) {
            File[] outputFiles = outputDir.listFiles();
            
            for(File file : outputFiles) {
               String name = file.getName();
               
               if(file.isFile() && name.endsWith(".js")) {
                  long lastModified = file.lastModified();
                  
                  if(outputTime < lastModified) {
                     outputTime = lastModified;
                  }
                  outputCount++;
               }
            }
         }
         if(sourceTime > outputTime || outputCount == 0) {
            ScriptCompiler compiler = new ScriptCompiler(CompilationLevel.SIMPLE_OPTIMIZATIONS);
            ScriptProcessor processor = new ScriptProcessor(compiler);
            ProcessBuilder builder = new ProcessBuilder(command);
            CompilerListener listener = new CompilerListener();
            ConsoleManager manager = new ConsoleManager(listener);
            
            manager.start();
            builder.directory(work);
            builder.redirectErrorStream(true);
            
            Process process = builder.start();
            
            manager.tail(process, "tsc");
            process.waitFor();
            
            if(outputDir.isDirectory()) {
               File[] outputFiles = outputDir.listFiles();
               
               for(File file : outputFiles) {
                  String name = file.getName();
                  
                  if(file.isFile() && name.endsWith(".js")) {
                     processor.process(file); // minify the source
                  }
               }
            }
         }
      }
   }
   
   private static class CompilerListener implements ConsoleListener {
      
      private final ProcessLogger logger;
      private final ProcessLog log;
      
      public CompilerListener() {
         this.log = new ConsoleLog();
         this.logger = new ProcessLogger(log);
      }

      @Override
      public void onUpdate(String process, String text) {
         try {
            String line = text.trim();
            logger.info(process + ": " + line);
         }catch(Exception e) {
            e.printStackTrace();
         }
      }
      
      @Override
      public void onUpdate(String process, String text, Throwable cause) {
         try {
            String line = text.trim();
            logger.info(process + ": " + line, cause);
         }catch(Exception e) {
            e.printStackTrace();
         }
      }
   }
}