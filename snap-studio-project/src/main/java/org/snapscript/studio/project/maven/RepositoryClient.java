package org.snapscript.studio.project.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;

import org.snapscript.studio.project.config.DependencyFileSet;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyFilter;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.resolution.DependencyResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.JavaScopes;
import org.sonatype.aether.util.filter.DependencyFilterUtils;

@Slf4j
public class RepositoryClient {
   
   private static final String EXTENSION_TYPE = "jar";

   private final List<RemoteRepository> repositories;
   private final Map<String, DependencyFileSet> cache;
   private final RepositoryFactory factory;
   private final RepositorySystem system;
   private final String path;

   public RepositoryClient(List<RemoteRepository> repositories, RepositorySystem system, RepositoryFactory factory, String path) {
      this.cache = new ConcurrentHashMap<String, DependencyFileSet>();
      this.repositories = repositories;
      this.factory = factory;
      this.system = system;
      this.path = path;
   }

   public DependencyFileSet resolve(String groupId, String artifactId, String version) throws Exception {
      String key = String.format("%s:%s:%s", groupId, artifactId, version);
      DependencyFileSet set = cache.get(key);
      
      if(set == null) {
         try {
            return download(key, groupId, artifactId, version);
         } catch(Exception e) {
            set = new DependencyFileSet(Collections.EMPTY_LIST, key, "Could not resolve '" + key + "'");
            
            if(log.isTraceEnabled()) {
               log.trace("Could not resolve '" + key + "'", e);
            }
         }
         cache.put(key, set);
      }
      return set;
   }
   
   private DependencyFileSet download(String key, String groupId, String artifactId, String version) throws Exception {
      List<File> files = new ArrayList<File>();
      Artifact artifact = new DefaultArtifact(groupId, artifactId, EXTENSION_TYPE, version);
      RepositorySystemSession session = factory.newRepositorySystemSession(system, path);
      DependencyFilter filter = DependencyFilterUtils.classpathFilter(JavaScopes.COMPILE);

      CollectRequest request = new CollectRequest();
      Dependency dependency = new Dependency(artifact, JavaScopes.COMPILE, true); // make it optional
      DependencyRequest dependencyRequest = new DependencyRequest(request, filter);
      
      request.setRoot(dependency);

      for (RemoteRepository repository : repositories) {
         request.addRepository(repository);
      }
      DependencyResult dependencyResult = system.resolveDependencies(session, dependencyRequest);
      List<ArtifactResult> artifactResults = dependencyResult.getArtifactResults();

      for (ArtifactResult artifactResult : artifactResults) {
         File localFile = artifactResult.getArtifact().getFile();
         File canonicalFile = localFile.getCanonicalFile();

         files.add(canonicalFile);
      }
      return new DependencyFileSet(files, key, null);
   }   
      
}