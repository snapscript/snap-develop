
package org.snapscript.develop.configuration;

import java.io.File;
import java.util.List;

import org.snapscript.develop.maven.RepositoryFactory;

public interface DependencyLoader {
   List<File> getDependencies(RepositoryFactory factory);
}
