package org.snapscript.studio;

import static org.snapscript.studio.configuration.WorkspaceConfiguration.JAR_FILE;
import static org.snapscript.studio.configuration.WorkspaceConfiguration.TEMP_PATH;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.snapscript.agent.ProcessMode;
import org.snapscript.studio.configuration.ProcessConfiguration;

public class ProcessLauncher {
   
   private final ProcessNameGenerator generator;
   private final Workspace workspace;
   
   public ProcessLauncher(Workspace workspace) {
      this.generator = new ProcessNameGenerator();
      this.workspace = workspace;
   }

   public ProcessDefinition launch(ProcessConfiguration configuration) throws Exception {
      int port = configuration.getPort();
      String host = configuration.getHost();
      String level = workspace.getLogger().getLevel();
      String name = generator.generate();
      String mode = ProcessMode.SCRIPT.name();
      String home = System.getProperty("java.home");
      File directory = workspace.createFile(TEMP_PATH);
      File file = new File(directory, JAR_FILE);
      String agent = file.getCanonicalPath();
      String java = String.format("%s%sbin%sjava", home, File.separatorChar, File.separatorChar);
      String resources = String.format("http://%s:%s/resource/", host, port);
      String classes = String.format("http://%s:%s/class/", host, port);
      Map<String, String> variables = configuration.getVariables();
      List<String> arguments = configuration.getArguments();
      String target = ProcessRunner.class.getCanonicalName();
      List<String> command = new ArrayList<String>();
      
      command.add(java);
      command.addAll(arguments);
      command.add("-jar");
      command.add(agent);
      command.add(classes);
      command.add(target);
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
      
      workspace.getLogger().info(name + ": " +command);
      builder.directory(directory);
      builder.redirectErrorStream(true);
      
      Process process = builder.start();
      return new ProcessDefinition(process, name);
   }
}