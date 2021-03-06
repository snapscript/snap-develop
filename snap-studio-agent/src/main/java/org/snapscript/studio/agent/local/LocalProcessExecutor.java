package org.snapscript.studio.agent.local;

import static org.snapscript.core.Reserved.DEFAULT_PACKAGE;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.snapscript.compile.Executable;
import org.snapscript.compile.ResourceCompiler;
import org.snapscript.compile.verify.VerifyError;
import org.snapscript.compile.verify.VerifyException;
import org.snapscript.core.ExpressionEvaluator;
import org.snapscript.core.module.FilePathConverter;
import org.snapscript.core.module.Path;
import org.snapscript.core.module.PathConverter;
import org.snapscript.core.scope.MapModel;
import org.snapscript.core.scope.Model;
import org.snapscript.studio.agent.ProcessContext;
import org.snapscript.studio.agent.ProcessMode;
import org.snapscript.studio.agent.ProcessModel;
import org.snapscript.studio.agent.cli.CommandLineUsage;
import org.snapscript.studio.agent.cli.CommandOption;
import org.snapscript.studio.agent.local.store.LocalStore;
import org.snapscript.studio.agent.local.store.LocalStoreBuilder;

public class LocalProcessExecutor {
   
   private final LocalStoreBuilder builder;
   private final PathConverter converter;
   
   public LocalProcessExecutor() {
      this.builder = new LocalStoreBuilder();
      this.converter = new FilePathConverter();
   }
   
   public void execute(LocalCommandLine line) throws Exception {
      ProcessContext context = createContext(line);
      Model model = createModel(line);
      String module = createModule(line);
      Executable executable = createExecutable(line, context);
      ExpressionEvaluator evaluator = context.getEvaluator();
      String evaluate = line.getEvaluation();
      Path script = line.getScript();
      Integer port = line.getPort();
      
      try {      
         CountDownLatch latch = new CountDownLatch(1);
         
         if(port != null && script != null) {
            LocalProcessController connector = new LocalProcessController(context, latch, script, port);
      
            if(!line.isWait()) {
               latch.countDown(); // if no suspend then count down
            }
            connector.start();
         } else {
            latch.countDown(); // no debugger
         }
         if(evaluate != null) {
            if(executable != null) {
               executable.execute(model, true); // do not execute
            }
            latch.await();
            evaluator.evaluate(model, evaluate, module);
         } else {
            if(line.isCheck()) {
               executable.execute(model, true); // do not execute
            } else {
               latch.await();
               executable.execute(model);
            }
         }
      } catch(VerifyException cause){
         List<VerifyError> errors = cause.getErrors();
         
         for(VerifyError error : errors) {
            System.err.println(error);
            System.err.flush();
         }
      } 
   }
   
   private ProcessContext createContext(LocalCommandLine line) throws Exception {
      List<? extends CommandOption> options = line.getOptions();
      String process = LocalNameGenerator.getProcess();
      LocalStore store = builder.create(line);
      String evaluate = line.getEvaluation();
      Path script = line.getScript();
      
      if(evaluate == null && script == null) {
         String message = String.format("--%s or --%s required", LocalOption.SCRIPT.name, LocalOption.SCRIPT.name);
         CommandLineUsage.usage(options, message);
      }
      try {
         String path = script.getPath();
         store.getInputStream(path);
      }catch(Exception cause) {
         String message = cause.getMessage();
         CommandLineUsage.usage(options, message);
      }
      return new ProcessContext(ProcessMode.REMOTE, store, process);
   }
   
   private Executable createExecutable(LocalCommandLine line, ProcessContext context) throws Exception {
      Path script = line.getScript();
      String file = script.getPath();
      ResourceCompiler compiler = context.getCompiler();

      return compiler.compile(file);
   }
   
   private Model createModel(LocalCommandLine line) throws Exception {
      Map<String, Object> values = new LinkedHashMap<String, Object>();
      Model model = new MapModel(values);  
      String[] arguments = line.getArguments();
      
      values.put(ProcessModel.SHORT_ARGUMENTS, arguments);
      values.put(ProcessModel.LONG_ARGUMENTS, arguments);

      return model;
   }
   
   private String createModule(LocalCommandLine line) throws Exception {
      Path script = line.getScript();
      
      if(script != null) {
         String file = script.getPath();
         return converter.createModule(file);
      }
      return DEFAULT_PACKAGE;
   }
}
