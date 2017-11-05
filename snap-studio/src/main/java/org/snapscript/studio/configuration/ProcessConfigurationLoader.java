package org.snapscript.studio.configuration;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.snapscript.studio.Workspace;

public class ProcessConfigurationLoader {
   
   private final Workspace workspace;
   
   public ProcessConfigurationLoader(Workspace workspace) {
      this.workspace = workspace;
   }

   public void load(ProcessConfiguration configuration) {
      try {
         Map<String, String> environment = configuration.getVariables();
         Map<String, String> variables = workspace.getEnvironmentVariables();
         List<String> arguments = configuration.getArguments();
         List<String> values = workspace.getArguments();

         if(variables != null) {
            Set<String> names = variables.keySet();
            
            for(String name : names) {
               String value = variables.get(name);
               environment.put(name, value);
            }
         }
         if(values != null) {
            for(String value : values) {
               String token = value.trim();
               
               if(!token.isEmpty()) {
                  arguments.add(token);
               }
            }
         }
      } catch(Exception e) {
         throw new IllegalStateException("Could not load configuration", e);
      }
   }

}