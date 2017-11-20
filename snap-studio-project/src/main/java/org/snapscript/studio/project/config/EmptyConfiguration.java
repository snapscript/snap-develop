package org.snapscript.studio.project.config;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EmptyConfiguration implements WorkspaceConfiguration {
   
   public EmptyConfiguration() {
      super();
   }

   @Override
   public List<File> getDependencies(List<Dependency> dependencies) {
      return Collections.emptyList();
   }
   
   @Override
   public Map<String, String> getEnvironmentVariables() {
      return Collections.emptyMap();
   }

   @Override
   public List<String> getArguments() {
      return Collections.emptyList();
   }
}