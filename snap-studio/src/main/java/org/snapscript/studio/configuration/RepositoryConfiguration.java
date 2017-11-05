package org.snapscript.studio.configuration;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.snapscript.studio.maven.RepositoryFactory;

public class RepositoryConfiguration implements WorkspaceConfiguration {
   
   private final Map<String, String> variables;
   private final List<String> arguments;
   private final RepositoryFactory factory;
   private final DependencyLoader loader;
   
   public RepositoryConfiguration(RepositoryFactory factory, DependencyLoader loader, Map<String, String> variables, List<String> arguments){
      this.variables = variables;
      this.arguments = arguments;
      this.loader = loader;
      this.factory = factory;
   }

   @Override
   public List<File> getDependencies(List<Dependency> dependencies) {
      if(loader == null) {
         throw new IllegalStateException("Could not resolve dependencies");
      }
      return loader.getDependencies(factory, dependencies);
   }
   
   @Override
   public Map<String, String> getEnvironmentVariables() {
      return variables;
   }

   @Override
   public List<String> getArguments() {
      return arguments;
   }
}