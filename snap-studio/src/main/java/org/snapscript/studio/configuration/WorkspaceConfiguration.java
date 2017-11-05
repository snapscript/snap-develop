package org.snapscript.studio.configuration;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface WorkspaceConfiguration {

   String CLASS_EXTENSION = ".class";
   String WORKSPACE_FILE = ".workspace";
   String TEMP_PATH = ".temp";
   String JAR_FILE = "agent.jar";
   
   List<File> getDependencies(List<Dependency> dependencies);
   Map<String, String> getEnvironmentVariables();
   List<String> getArguments();
}