package org.snapscript.studio.service.agent;

import static org.snapscript.studio.project.config.WorkspaceConfiguration.JAR_FILE;
import static org.snapscript.studio.project.config.WorkspaceConfiguration.TEMP_PATH;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.snapscript.studio.agent.RunMode;
import org.snapscript.studio.project.HomeDirectory;
import org.snapscript.studio.project.Workspace;
import org.snapscript.studio.project.config.ProcessConfiguration;
import org.snapscript.studio.service.ProcessDefinition;
import org.snapscript.studio.service.ProcessLauncher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DebugAgentLauncher implements ProcessLauncher {
   
   private final DebugAgentFilter filter;
   private final Workspace workspace;
   
   public DebugAgentLauncher(DebugAgentFilter filter, Workspace workspace) {     
      this.workspace = workspace;
      this.filter = filter;
   }

   public ProcessDefinition launch(ProcessConfiguration configuration) throws Exception {
      int port = configuration.getPort();
      String host = configuration.getHost();
      String level = "DEBUG";//workspace.getLogger();
      String name = filter.generate();
      String mode = RunMode.SCRIPT.name();
      String home = System.getProperty("java.home");
      File directory = HomeDirectory.getPath(TEMP_PATH);
      File file = new File(directory, JAR_FILE);
      String agent = file.getCanonicalPath();
      String java = String.format("%s%sbin%sjava", home, File.separatorChar, File.separatorChar);
      String resources = String.format("http://%s:%s/download/", host, port);
      String classes = String.format("http://%s:%s/class/", host, port);
      Map<String, String> variables = configuration.getVariables();
      List<String> arguments = configuration.getArguments();
      String target = DebugAgentRunner.class.getCanonicalName();
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
      
      log.info(name + ": " +command);
      builder.directory(directory);
      builder.redirectErrorStream(true);
      
      Process process = builder.start();
      return new ProcessDefinition(process, name);
   }
}