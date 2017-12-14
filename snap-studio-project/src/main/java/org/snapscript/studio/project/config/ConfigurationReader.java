package org.snapscript.studio.project.config;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.EMPTY_MAP;
import static org.snapscript.studio.project.config.ProjectConfiguration.PROJECT_FILE;
import static org.snapscript.studio.project.config.WorkspaceConfiguration.WORKSPACE_FILE;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import lombok.extern.slf4j.Slf4j;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.Transient;
import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.util.Dictionary;
import org.simpleframework.xml.util.Entry;
import org.snapscript.studio.project.FileSystem;
import org.snapscript.studio.project.Project;
import org.snapscript.studio.project.ProjectLayout;
import org.snapscript.studio.project.Workspace;
import org.snapscript.studio.project.maven.RepositoryClient;
import org.snapscript.studio.project.maven.RepositoryFactory;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.repository.RemoteRepository;

@Slf4j
public class ConfigurationReader {
   
   private final AtomicReference<WorkspaceConfiguration> workspaceReference;
   private final Map<String, ProjectConfiguration> projectReference;
   private final ConfigurationFilter filter;
   private final RepositoryFactory factory;
   private final Persister persister;
   private final Workspace workspace;
   
   public ConfigurationReader(Workspace workspace) {
      this.projectReference = new ConcurrentHashMap<String, ProjectConfiguration>();
      this.workspaceReference = new AtomicReference<WorkspaceConfiguration>();
      this.factory = new RepositoryFactory();
      this.filter = new ConfigurationFilter();
      this.persister = new Persister(filter);
      this.workspace = workspace;
   }

   public WorkspaceConfiguration loadWorkspaceConfiguration() {
      WorkspaceConfiguration configuration = workspaceReference.get();
      
      if(configuration == null) {
         try {
            File file = workspace.createFile(WORKSPACE_FILE);
            
            if(file.exists()) {
               WorkspaceDefinition details = persister.read(WorkspaceDefinition.class, file);
               Map<String, String> locations = details.getRepositoryLocations();
               Map<String, String> variables = details.getEnvironmentVariables();
               List<String> arguments = details.getArguments();
               Set<String> repositories = locations.keySet();
               
               for(String repository : repositories) {
                  String location = locations.get(repository);
                  log.info("Repository: '" + repository + "' -> '" + location + "'");
               }
               configuration = new WorkspaceContext(factory, details, variables, arguments);
               workspaceReference.set(configuration);
               return configuration;
            }
         }catch(Exception e) {
            throw new IllegalStateException("Could not read configuration", e);
         }
         return new WorkspaceContext(factory, null, EMPTY_MAP, EMPTY_LIST);
      }
      return configuration;
   }  
   
   public ProjectConfiguration loadProjectConfiguration(String name) {
      try {
         ProjectConfiguration configuration = projectReference.get(name);
         
         if(isProjectConfigurationStale(name)) {
            Project project = workspace.createProject(name);
            FileSystem fileSystem = project.getFileSystem();
            File file = fileSystem.getFile(PROJECT_FILE);
            ProjectDefinition definition = persister.read(ProjectDefinition.class, file);
            
            projectReference.put(name, definition);
            return definition;
         } 
         if(configuration != null) {
            return configuration;
         }
         log.info("Project '" + name + "' does not contain a .project file");
      }catch(Exception e) {
         throw new IllegalStateException("Could not read .project file", e);
      }
      return new ProjectDefinition();
   }
   
   private boolean isProjectConfigurationStale(String name) {
      ProjectConfiguration configuration = projectReference.get(name);
      
      if(configuration == null) {
         return true;
      }
      Project project = workspace.createProject(name);
      FileSystem fileSystem = project.getFileSystem();
      File file = fileSystem.getFile(PROJECT_FILE);
      
      if(file.exists()) {
         long lastModified = file.lastModified();
         long configurationModification = configuration.getLastModifiedTime();
         
         if(lastModified > configurationModification) {
            return file.exists() && file.length() > 0;
         }
      }
      return false;
   }
   
   @Root
   private static class ProjectDefinition implements ProjectConfiguration {
      
      @Path("dependencies")
      @ElementList(entry="dependency", required=false, inline=true)
      private List<DependencyDefinition> dependencies;
      
      @ElementList(entry="variable", required=false)
      private Dictionary<VariableDefinition> properties;
      
      @Transient
      private Map<String, Object> attributes;
      
      @ElementList(entry="path", required=false)
      private List<String> source;
      
      private long lastModified;
      
      public ProjectDefinition() {
         this.lastModified = System.currentTimeMillis();
         this.dependencies = new ArrayList<DependencyDefinition>();
         this.properties = new Dictionary<VariableDefinition>();
         this.attributes = new ConcurrentHashMap<String, Object>();
         this.source = new ArrayList<String>();
      }

      @Override
      public Map<String, String> getProperties() {
         Map<String, String> map = new LinkedHashMap<String, String>();
         
         if(properties != null) {
            for(VariableDefinition data : properties) {
               map.put(data.name, data.value);
            }
         }
         return map;
      }
      
      @Override
      public List<Dependency> getDependencies() {
         return Collections.<Dependency>unmodifiableList(dependencies);
      }

      @Override
      public ProjectLayout getProjectLayout() {
         return new ProjectLayout(source.toArray(new String[]{}));
      }
      
      @Override
      public long getLastModifiedTime() {
         return lastModified;
      }

      @Override
      public <T> T getAttribute(String name) {
         return (T)attributes.get(name);
      }

      @Override
      public void setAttribute(String name, Object value) {
         if(value != null) {
            attributes.put(name, value);
         } else {
            attributes.remove(name);
         }
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
      public List<DependencyFile> getDependencies(RepositoryFactory factory, List<Dependency> dependencies) {
         List<DependencyFile> files = new ArrayList<DependencyFile>();
         Set<String> done = new HashSet<String>();
         
         try {
            if(repository != null) {
               RepositoryClient client = repository.getClient(factory);
   
               if(dependencies != null) {
                  for (Dependency dependency : dependencies) {
                     String groupId = dependency.getGroupId();
                     String artifactId = dependency.getArtifactId();
                     String version = dependency.getVersion();
                     String key = String.format("%s:%s:%s", groupId, artifactId, version);
                     
                     if(done.add(key)) { // has this already been resolved
                        DependencyFileSet set = client.resolve(groupId, artifactId, version);
                        List<File> matches = set.getFiles();
                        String message = set.getMessage();
   
                        if(matches.isEmpty()) {
                           DependencyFile file = new DependencyFile(null, message);
                           files.add(file);
                        } else {
                           for (File match : matches) {
                              String path = match.getCanonicalPath();
                              
                              if(done.add(path)) { // has file already been added
                                 if(match.exists()) {
                                    DependencyFile file = new DependencyFile(match);
                                    files.add(file);
                                 } else {
                                    DependencyFile file = new DependencyFile(match, "Could not resolve " + key);
                                    files.add(file);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         } catch(Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
         }
         return files;
      }
      
      public Map<String, String> getRepositoryLocations() {
         Map<String, String> locations = new LinkedHashMap<String, String>();
         
         if(repository != null && repository.repositories != null) {
            for(LocationDefinition definition : repository.repositories) {
               locations.put(definition.name, definition.location);
            }
         }
         return locations;
      }

      public Map<String, String> getEnvironmentVariables() {
         Map<String, String> variables = new LinkedHashMap<String, String>();
         
         if(environment != null) {
            for(VariableDefinition data : environment) {
               variables.put(data.name, data.value);
            }
         }
         return variables;
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
   
   @Root
   private static class CommandTemplate implements Entry {
      
      @Attribute
      private OperatingSystem type;
      
      @Text
      private String value;
      
      @Override
      public String getName() {
         return type.name();
      }
   }
}