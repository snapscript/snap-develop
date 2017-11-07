package org.snapscript.studio.configuration;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.snapscript.studio.resource.project.ProjectLayout;

import com.google.common.reflect.ClassPath.ClassInfo;

public interface ProjectConfiguration {
   
   String PROJECT_FILE = ".project";
   String CLASSPATH_FILE = ".classpath";
   
   List<Dependency> getDependencies();
   Map<String, String> getEnvironmentVariables();
   Set<ClassInfo> getAllClasses();
   ProjectLayout getProjectLayout();
   long getLastModifiedTime();
}
