package org.snapscript.service;

import static org.snapscript.core.Reserved.DEFAULT_PACKAGE;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import org.snapscript.common.store.Store;
import org.snapscript.common.thread.ThreadPool;
import org.snapscript.compile.Compiler;
import org.snapscript.compile.Executable;
import org.snapscript.compile.ResourceCompiler;
import org.snapscript.compile.StoreContext;
import org.snapscript.core.Context;
import org.snapscript.core.ExpressionEvaluator;
import org.snapscript.core.FilePathConverter;
import org.snapscript.core.Model;
import org.snapscript.core.Path;
import org.snapscript.core.PathConverter;

public class ScriptExecutor implements Callable<Context> {

   private final CommandLine line;
   
   public ScriptExecutor(CommandLine line) {
      this.line = line;
   }
   
   @Override
   public Context call() throws Exception {
      Store store = line.getStore();
      String evaluate = line.getEvaluation();
      Path script = line.getScript();
      Model model = line.getModel();
      String module = DEFAULT_PACKAGE;
      
      if(evaluate == null && script == null) {
         System.err.println("Neither --evaluate or --script have been specified");
         System.exit(0);
      }
      PathConverter converter = new FilePathConverter();
      Executor executor = new ThreadPool(8);
      Context context = new StoreContext(store, executor);
      Compiler compiler = new ResourceCompiler(context);
      Executable executable = null;
      
      if(script != null) {
         String file = script.getPath();
         
         module = converter.createModule(file);
         executable = compiler.compile(file);
      }
      if(evaluate != null) {
         ExpressionEvaluator evaluator = context.getEvaluator();
         evaluator.evaluate(model, evaluate, module);
      } else {
         executable.execute(model);
      }
      return context;
   }
}