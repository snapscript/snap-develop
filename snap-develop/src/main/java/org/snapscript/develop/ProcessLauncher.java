package org.snapscript.develop;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.snapscript.agent.ProcessMode;
import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.develop.configuration.Configuration;
import org.snapscript.develop.configuration.ProcessConfiguration;
import org.snapscript.develop.http.loader.RemoteProcessBuilder;
import org.snapscript.develop.http.loader.RemoteProcessLauncher;

public class ProcessLauncher {
   
   private final ProcessNameGenerator generator;
   private final ProcessEventChannel channel;
   private final ProcessLogger logger;
   private final Workspace workspace;
   
   public ProcessLauncher(ProcessEventChannel channel, ProcessLogger logger, Workspace workspace) {
      this.generator = new ProcessNameGenerator();
      this.workspace = workspace;
      this.channel = channel;
      this.logger = logger;
   }

   public ProcessDefinition launch(ProcessConfiguration configuration) throws Exception {
      int port = configuration.getPort();
      String host = configuration.getHost();
      String level = logger.getLevel();
      String name = generator.generate();
      String mode = ProcessMode.ATTACHED.name();
      String home = System.getProperty("java.home");
      String java = String.format("%s%sbin%sjava", home, File.separatorChar, File.separatorChar);
      String resources = String.format("http://%s:%s/resource/", host, port);
      String classes = String.format("http://%s:%s/class/", host, port);
      Map<String, String> variables = configuration.getVariables();
      List<String> arguments = configuration.getArguments();
      String launcher = RemoteProcessLauncher.class.getCanonicalName();
      String target = ProcessRunner.class.getCanonicalName();
      String dependencies = write(configuration);
      List<String> command = new ArrayList<String>();
      
      command.add(java);
      command.addAll(arguments);
      command.add("-cp");
      command.add(".");
      command.add(launcher);
      command.add(classes);
      command.add(target);
      command.add(dependencies);
      command.add("org.snapscript.");
      command.add(resources);
      command.add(name);
      command.add(level);
      command.add(mode);

      ProcessBuilder builder = new ProcessBuilder(command);
      
      if(!variables.isEmpty()) {
         Map<String, String> environment = builder.environment();
         environment.putAll(variables);
      }
      File directory = workspace.create(RemoteProcessBuilder.TEMP_PATH);
      
      logger.info(name + ": " +command);
      builder.directory(directory);
      builder.redirectErrorStream(true);
      
      Process process = builder.start();
      return new ProcessDefinition(process, name);
   }
   
   private String write(ProcessConfiguration configuration) throws Exception {
      File projectFile = workspace.create(Configuration.PROJECT_FILE);
      File classPathFile = workspace.create(Configuration.CLASSPATH_FILE);
      String classPath = configuration.getClassPath();
      
      if(classPath == null) {
         classPath = System.getProperty("java.class.path");
      }
      String[] dependencies = classPath.split(File.pathSeparator);
      
      if(!classPathFile.exists()) {
         FileWriter writer = new FileWriter(classPathFile);
         PrintWriter printer = new PrintWriter(writer);
         
         for(String dependency : dependencies) {
            printer.println(dependency);
         }
         printer.close();
         logger.info("Created " + classPathFile);
      } else if(projectFile.exists()) {
         long projectFileChange = projectFile.lastModified();
         long classPathFileChange = classPathFile.lastModified();
         
         if(projectFileChange > classPathFileChange) {
            FileWriter writer = new FileWriter(classPathFile);
            PrintWriter printer = new PrintWriter(writer);
            
            for(String dependency : dependencies) {
               printer.println(dependency);
            }
            printer.close();
            logger.info("Updated " + classPathFile + " from " + projectFile);
         }
      }
      return classPathFile.getCanonicalPath();
   }
}
