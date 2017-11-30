package org.snapscript.studio.project.config;

import java.util.List;

import org.snapscript.studio.project.maven.RepositoryFactory;

public interface DependencyLoader {
   List<DependencyFile> getDependencies(RepositoryFactory factory, List<Dependency> dependencies);
}