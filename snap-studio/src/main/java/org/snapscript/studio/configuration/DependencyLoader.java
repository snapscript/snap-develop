package org.snapscript.studio.configuration;

import java.io.File;
import java.util.List;

import org.snapscript.studio.maven.RepositoryFactory;

public interface DependencyLoader {
   List<File> getDependencies(RepositoryFactory factory, List<Dependency> dependencies);
}