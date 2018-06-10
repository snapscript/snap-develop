package org.snapscript.studio.cli;

import static org.snapscript.core.Reserved.DEFAULT_PACKAGE;

import java.io.File;
import java.util.List;

import org.snapscript.compile.Executable;
import org.snapscript.compile.ResourceCompiler;
import org.snapscript.compile.verify.VerifyError;
import org.snapscript.compile.verify.VerifyException;
import org.snapscript.core.ExpressionEvaluator;
import org.snapscript.core.module.FilePathConverter;
import org.snapscript.core.module.Path;
import org.snapscript.core.module.PathConverter;
import org.snapscript.core.scope.Model;
import org.snapscript.studio.agent.DebugContext;
import org.snapscript.studio.agent.RunMode;
import org.snapscript.studio.cli.debug.DebugRequestListener;
import org.snapscript.studio.cli.load.FileClassLoader;
import org.snapscript.studio.cli.store.ProcessStore;

public class CommandLineInterpreter {

   public static void main(String[] options) throws Exception {
      CommandLineParser parser = new CommandLineParser();
      CommandLine line = parser.parse(options);
      List<File> classpath = line.getClasspath();
      boolean debug = line.isDebug();
      
      try {
         FileClassLoader.update(classpath, debug);
         line.validate();
      }catch(Exception cause) {
         String message = cause.getMessage();
         CommandLineUsage.usage(message);
      }
      execute(line);
   }
   
   public static void execute(CommandLine line) throws Exception {
      ProcessStore store = line.getStore();
      String evaluate = line.getEvaluation();
      Path script = line.getScript();
      Model model = line.getModel();
      String system = line.getSystem();
      Integer port = line.getPort();
      String process = line.getProcess();
      String module = DEFAULT_PACKAGE;
      
      if(evaluate == null && script == null) {
         CommandLineUsage.usage();
      }
      DebugContext context = new DebugContext(RunMode.REMOTE, store, process, system);
      PathConverter converter = new FilePathConverter();
      Executable executable = null;
      
      try {         
         if(port != null && script != null) {
            DebugRequestListener connector = new DebugRequestListener(context, script, port);
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