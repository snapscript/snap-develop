package org.snapscript.studio.agent.local;

import static org.snapscript.core.Reserved.DEFAULT_PACKAGE;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import org.snapscript.studio.agent.cli.CommandLineUsage;
import org.snapscript.studio.agent.cli.CommandOption;
import org.snapscript.studio.agent.local.store.LocalStore;
import org.snapscript.studio.agent.local.store.LocalStoreBuilder;

public class LocalProcessExecutor {
   
   private static final String ARGUMENTS = "args";
   
   private final LocalStoreBuilder builder;
   private final PathConverter converter;
   
   public LocalProcessExecutor() {
      this.builder = new LocalStoreBuilder();
      this.converter = new FilePathConverter();
   }
   
   public void execute(LocalCommandLine line) throws Exception {
      List<? extends CommandOption> options = line.getOptions();
      String process = LocalNameGenerator.getProcess();
      LocalStore store = builder.create(line);
      String evaluate = line.getEvaluation();
      String system = line.getSystem();
      Path script = line.getScript();
      Integer port = line.getPort();
      String module = DEFAULT_PACKAGE;
      
      if(evaluate == null && script == null) {
         CommandLineUsage.usage(options);
      }
      try {
         String path = script.getPath();
         store.getInputStream(path);
      }catch(Exception cause) {
         String message = cause.getMessage();
         CommandLineUsage.usage(options, message);
      }
      ProcessContext context = new ProcessContext(ProcessMode.REMOTE, store, process, system);
      Map<String, Object> values = new LinkedHashMap<String, Object>();
      Model model = new MapModel(values);
      Executable executable = null;
      
      try {      
         String[] arguments = line.getArguments();
         
         values.put(ARGUMENTS, arguments);
         
         if(port != null && script != null) {
            LocalProcessController connector = new LocalProcessController(context, script, port);
            connector.start();
         }
         if(script != null) {
            String file = script.getPath();
            ResourceCompiler compiler = context.getCompiler();
            
            module = converter.createModule(file);
            executable = compiler.compile(file);
         }
         if(evaluate != null) {
            ExpressionEvaluator evaluator = context.getEvaluator();
            
            executable.execute(model, true); // do not execute
            evaluator.evaluate(model, evaluate, module);
         } else {
            if(line.isCheck()) {
               executable.execute(model, true); // do not execute
            } else {
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
}
