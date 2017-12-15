package org.snapscript.studio.project.generate;

import org.snapscript.studio.project.Project;

public interface ConfigFileGenerator {
   ConfigFile generateConfig(Project project);
   ConfigFile parseConfig(Project project, String source);
   String getConfigFilePath();
}
