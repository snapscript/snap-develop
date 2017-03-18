
package org.snapscript.develop.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.core.Validate;
import org.simpleframework.xml.util.Dictionary;
import org.simpleframework.xml.util.Entry;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.develop.Workspace;
import org.snapscript.develop.maven.RepositoryClient;
import org.snapscript.develop.maven.RepositoryFactory;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.repository.RemoteRepository;

public class ConfigurationReader {
   
   private final AtomicReference<Configuration> reference;
   private final ConfigurationFilter filter;
   private final RepositoryFactory factory;
   private final Persister persister;
   private final Workspace workspace;
   
   public ConfigurationReader(ProcessLogger logger, Workspace workspace) {
      this.reference = new AtomicReference<Configuration>();
      this.factory = new RepositoryFactory(logger);
      this.filter = new ConfigurationFilter();
      this.persister = new Persister(filter);
      this.workspace = workspace;
   }

   public Configuration load() {
      Configuration configuration = reference.get();
      
      if(configuration == null) {
         try {
            File file = workspace.create(Configuration.PROJECT_FILE);
            
            if(file.exists()) {
               ProjectDefinition details = persister.read(ProjectDefinition.class, file);
               Map<String, String> variables = details.getVariables();
               List<String> arguments = details.getArguments();
               
               configuration = new RepositoryConfiguration(factory, details, variables, arguments);
               reference.set(configuration);
               return configuration;
            }
         }catch(Exception e) {
            throw new IllegalStateException("Could not read configuration", e);
         }
         return new EmptyConfiguration();
      }
      return configuration;
   }  
   

   
   @Root
   private static class ProjectDefinition implements DependencyLoader {
      
      @Element(name="repository", required=false)
      private RepositoryDefinition repository;
      
      @Path("dependencies")
      @ElementList(entry="dependency", required=false, inline=true)
      private List<DependencyDefinition> dependencies;
      
      @ElementList(entry="variable", required=false)
      private Dictionary<VariableDefinition> environment;
      
      @ElementList(entry="argument", required=false)
      private List<String> arguments;
      
      @Validate
      public void validate() {
         if(dependencies != null) {
            if(repository == null) {
               throw new IllegalStateException("No repository has been defined");
            }
         }
      }

      public Map<String, String> getVariables() {
         Map<String, String> map = new LinkedHashMap<String, String>();
         
         if(environment != null) {
            for(VariableDefinition data : environment) {
               map.put(data.name, data.value);
            }
         }
         return map;
      }
      
      @Override
      public List<File> getDependencies(RepositoryFactory factory) {
         List<File> files = new ArrayList<File>();
      
         try {
            RepositoryClient client = repository.getClient(factory);

            if(dependencies != null) {
               for (DependencyDefinition dependency : dependencies) {
                  List<File> matches = client.resolve(dependency.groupId, dependency.artifactId, dependency.version);

                  for (File match : matches) {
                     if (!match.exists()) {
                        throw new IllegalStateException("Could not resolve file " + match);
                     }
                     files.add(match);
                  }
               }
            }
         } catch(Exception e) {
            throw new IllegalStateException("Could not resolve dependencies", e);
         }
         return files;
      }
      
      public List<String> getArguments() {
         if(arguments != null) {
            return arguments;
         }
         return Collections.emptyList();
      }
   }
   
   
   @Root
   private static class RepositoryDefinition {
      
      @Attribute
      private String path;
      
      @ElementList(entry="location", inline=true)
      private List<LocationDefinition> repositories;
      
      public RepositoryClient getClient(RepositoryFactory factory) {
         List<RemoteRepository> list = new ArrayList<RemoteRepository>();
         
         for(LocationDefinition repository : repositories) {
            RemoteRepository remote = factory.newRemoteRepository(repository.name, "default", repository.location);
            list.add(remote);
         }
         RepositorySystem system = factory.newRepositorySystem();
         return new RepositoryClient(list, system, factory, path);
      }
   }
   
   @Root
   private static class LocationDefinition implements Entry {
      
      @Text
      private String location;
      
      @Attribute
      private String name;
      
      public LocationDefinition() {
         super();
      }

      @Override
      public String getName() {
         return name;
      }
   }
   
   @Root
   private static class DependencyDefinition {

      @Element
      private String groupId;
      
      @Element
      private String artifactId;
      
      @Element
      private String version;
      
      public DependencyDefinition() {
         super();
      }
   }
   
   @Root
   private static class VariableDefinition implements Entry {
      
      @Attribute
      private String name;
      
      @Text
      private String value;
      
      @Commit
      public void update(Map session) {
         session.put(name, value);
      }
      
      @Override
      public String getName() {
         return name;
      }
   }
}
