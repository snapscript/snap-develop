package org.snapscript.studio.cli;

import static org.snapscript.core.Reserved.DEFAULT_PACKAGE;

import java.util.List;
import java.util.concurrent.Executor;

import org.snapscript.common.store.Store;
import org.snapscript.common.thread.ThreadPool;
import org.snapscript.compile.Compiler;
import org.snapscript.compile.Executable;
import org.snapscript.compile.ResourceCompiler;
import org.snapscript.compile.StoreContext;
import org.snapscript.compile.verify.VerifyException;
import org.snapscript.compile.verify.VerifyError;
import org.snapscript.core.Context;
import org.snapscript.core.ExpressionEvaluator;
import org.snapscript.core.module.FilePathConverter;
import org.snapscript.core.module.Path;
import org.snapscript.core.module.PathConverter;
import org.snapscript.core.scope.Model;

public class ScriptExecutor {

   private final CommandLine line;
   
   public ScriptExecutor(CommandLine line) {
      this.line = line;
   }
  
   public void execute() throws Exception {
      Store store = line.getStore();
      String evaluate = line.getEvaluation();
      Path script = line.getScript();
      Model model = line.getModel();
      String module = DEFAULT_PACKAGE;
      
      if(evaluate == null && script == null) {
         CommandLineUsage.usage();
      }
      PathConverter converter = new FilePathConverter();
      Executor executor = new ThreadPool(8);
      Context context = new StoreContext(store, executor);
      Compiler compiler = new ResourceCompiler(context);
      Executable executable = null;
      
      try {
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
      } catch(VerifyException cause){
         List<VerifyError> errors = cause.getErrors();
         
         for(VerifyError error : errors) {
            System.err.println(error);
            System.err.flush();
         }
      } 
   }
}