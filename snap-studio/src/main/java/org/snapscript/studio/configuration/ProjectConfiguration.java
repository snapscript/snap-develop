package org.snapscript.studio.configuration;

import java.util.List;
import java.util.Map;

public interface ProjectConfiguration {
   
   String PROJECT_FILE = ".project";
   String CLASSPATH_FILE = ".classpath";
   
   List<Dependency> getDependencies();
   Map<String, String> getEnvironmentVariables();
}
