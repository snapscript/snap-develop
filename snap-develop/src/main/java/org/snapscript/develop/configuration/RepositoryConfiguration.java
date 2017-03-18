
package org.snapscript.develop.configuration;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.snapscript.develop.maven.RepositoryFactory;

public class RepositoryConfiguration implements Configuration {
   
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
   public Map<String, String> getVariables() {
      return variables;
   }

   @Override
   public List<File> getDependencies() {
      return loader.getDependencies(factory);
   }

   @Override
   public List<String> getArguments() {
      return arguments;
   }
}