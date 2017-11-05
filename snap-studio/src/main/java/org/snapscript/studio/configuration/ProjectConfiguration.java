package org.snapscript.studio.configuration;

import java.util.List;
import java.util.Map;

import org.snapscript.studio.resource.project.ProjectLayout;

public interface ProjectConfiguration {
   
   String PROJECT_FILE = ".project";
   String CLASSPATH_FILE = ".classpath";
   
   List<Dependency> getDependencies();
   Map<String, String> getEnvironmentVariables();
   ProjectLayout getProjectLayout();
   long getLastModifiedTime();
}
