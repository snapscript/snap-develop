package org.snapscript.studio.configuration;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.EMPTY_MAP;
import static org.snapscript.studio.configuration.ProjectConfiguration.PROJECT_FILE;
import static org.snapscript.studio.configuration.WorkspaceConfiguration.WORKSPACE_FILE;

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
import org.simpleframework.xml.util.Dictionary;
import org.simpleframework.xml.util.Entry;
import org.snapscript.studio.Workspace;
import org.snapscript.studio.maven.RepositoryClient;
import org.snapscript.studio.maven.RepositoryFactory;
import org.snapscript.studio.resource.project.Project;
import org.snapscript.studio.resource.project.ProjectFileSystem;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.repository.RemoteRepository;

public class ConfigurationReader {
   
   private final AtomicReference<WorkspaceConfiguration> reference;
   private final ConfigurationFilter filter;
   private final RepositoryFactory factory;
   private final Persister persister;
   private final Workspace workspace;
   
   public ConfigurationReader(Workspace workspace) {
      this.reference = new AtomicReference<WorkspaceConfiguration>();
      this.factory = new RepositoryFactory(workspace);
      this.filter = new ConfigurationFilter();
      this.persister = new Persister(filter);
      this.workspace = workspace;
   }

   public WorkspaceConfiguration loadWorkspaceConfiguration() {
      WorkspaceConfiguration configuration = reference.get();
      
      if(configuration == null) {
         try {
            File file = workspace.createFile(WORKSPACE_FILE);
            
            if(file.exists()) {
               WorkspaceDefinition details = persister.read(WorkspaceDefinition.class, file);
               Map<String, String> variables = details.getVariables();
               List<String> arguments = details.getArguments();
               
               configuration = new RepositoryConfiguration(factory, details, variables, arguments);
               reference.set(configuration);
               return configuration;
            }
         }catch(Exception e) {
            throw new IllegalStateException("Could not read configuration", e);
         }
         return new RepositoryConfiguration(factory, null, EMPTY_MAP, EMPTY_LIST);
      }
      return configuration;
   }  
   
   public ProjectConfiguration loadProjectConfiguration(String name) {
      try {
         Project project = workspace.createProject(name);
         ProjectFileSystem fileSystem = project.getFileSystem();
         File file = fileSystem.getFile(PROJECT_FILE);
         
         if(file.exists()) {
            return persister.read(ProjectDefinition.class, file);
         } else {
            workspace.getLogger().info("Project '" + name + "' does not contain a .project file");
         }
      }catch(Exception e) {
         throw new IllegalStateException("Could not read configuration", e);
      }
      return new ProjectDefinition();
   }
   
   @Root
   private static class ProjectDefinition implements ProjectConfiguration {
      
      @Path("dependencies")
      @ElementList(entry="dependency", required=false, inline=true)
      private List<DependencyDefinition> dependencies;
      
      @ElementList(entry="variable", required=false)
      private Dictionary<VariableDefinition> environment;
      
      public ProjectDefinition() {
         this.dependencies = new ArrayList<DependencyDefinition>();
         this.environment = new Dictionary<VariableDefinition>();
      }

      public Map<String, String> getEnvironmentVariables() {
         Map<String, String> map = new LinkedHashMap<String, String>();
         
         if(environment != null) {
            for(VariableDefinition data : environment) {
               map.put(data.name, data.value);
            }
         }
         return map;
      }
      
      public List<Dependency> getDependencies() {
         return Collections.<Dependency>unmodifiableList(dependencies);
      }
   }
   
   @Root
   private static class WorkspaceDefinition implements DependencyLoader {
      
      @Element(name="repository", required=false)
      private RepositoryDefinition repository;
      
      @ElementList(entry="variable", required=false)
      private Dictionary<VariableDefinition> environment;
      
      @ElementList(entry="argument", required=false)
      private List<String> arguments;
      
      public WorkspaceDefinition() {
         this.environment = new Dictionary<VariableDefinition>();
         this.arguments = new ArrayList<String>();
      }
      
      @Override
      public List<File> getDependencies(RepositoryFactory factory, List<Dependency> dependencies) {
         List<File> files = new ArrayList<File>();
      
         try {
            RepositoryClient client = repository.getClient(factory);

            if(dependencies != null) {
               for (Dependency dependency : dependencies) {
                  String groupId = dependency.getGroupId();
                  String artifactId = dependency.getArtifactId();
                  String version = dependency.getVersion();
                  List<File> matches = client.resolve(groupId, artifactId, version);

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

      public Map<String, String> getVariables() {
         Map<String, String> map = new LinkedHashMap<String, String>();
         
         if(environment != null) {
            for(VariableDefinition data : environment) {
               map.put(data.name, data.value);
            }
         }
         return map;
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
   private static class DependencyDefinition implements Dependency {

      @Element
      private String groupId;
      
      @Element
      private String artifactId;
      
      @Element
      private String version;
      
      public DependencyDefinition() {
         super();
      }

      @Override
      public String getGroupId() {
         return groupId;
      }

      @Override
      public String getArtifactId() {
         return artifactId;
      }

      @Override
      public String getVersion() {
         return version;
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