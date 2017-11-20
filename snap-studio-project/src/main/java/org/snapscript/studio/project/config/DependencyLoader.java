package org.snapscript.studio.project.config;

import java.io.File;
import java.util.List;

import org.snapscript.studio.project.maven.RepositoryFactory;

public interface DependencyLoader {
   List<File> getDependencies(RepositoryFactory factory, List<Dependency> dependencies);
}