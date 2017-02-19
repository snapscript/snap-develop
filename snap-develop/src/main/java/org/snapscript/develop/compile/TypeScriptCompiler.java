/*
 * TypeScriptCompiler.java December 2016
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

public class TypeScriptCompiler {
   
   private static final String DEFAULT_COMPILER = "src\\main\\typescript\\tsc.js";
   private static final String DEFAULT_NODE = "C:\\Program Files\\nodejs\\node.exe";
   
   private final File compiler;
   private final File node;
   private final File root;
   
   public TypeScriptCompiler(String compiler, String node) {
      this.root = new File(".");
      this.node = new File(node);
      this.compiler = new File(root, compiler);
   }
   
   public synchronized void compile(File sourceDir, File outputFile) throws Exception {
      if(!sourceDir.exists()) {
         throw new IOException("Source directory '" +sourceDir+"' does not exist");
      }
      if(!sourceDir.isDirectory()) {
         throw new IOException("Source directory '"+sourceDir+"' is actually a file");
      }
      if(node.exists() && compiler.exists()) {
         List<String> command = new ArrayList<String>();
         
         command.add(node.getCanonicalPath());
         command.add(compiler.getCanonicalPath()); 
         
         if(outputFile.isDirectory()) {
            command.add("--outDir");
            command.add(outputFile.getCanonicalPath());
         }else {
            command.add("--outFile");
            command.add(outputFile.getCanonicalPath());
         }
         File[] sourceFiles = sourceDir.listFiles();
         File work = compiler.getParentFile();
         long outputTime = outputFile.lastModified();
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
         if(outputFile.isDirectory()) {
            File[] outputFiles = outputFile.listFiles();
            
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
         } else {
            outputCount = 1;
         }
         if(sourceTime > outputTime || outputCount == 0) {
            ProcessBuilder builder = new ProcessBuilder(command);
            CompilerListener listener = new CompilerListener();
            ConsoleManager manager = new ConsoleManager(listener);
            
            manager.start();
            builder.directory(work);
            builder.redirectErrorStream(true);
            
            Process process = builder.start();
            
            manager.tail(process, "tsc");
            process.waitFor();
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

   public static void main(String[] list) throws Exception {
      File root = new File(".");
      File output = new File(root, "src\\main\\resources\\resource\\ts\\build\\all.js");
      File sourceDir = new File(root, "src\\main\\resources\\resource\\ts");
      TypeScriptCompiler compiler = new TypeScriptCompiler(DEFAULT_COMPILER, DEFAULT_NODE);
      compiler.compile(sourceDir, output);
   }
}
