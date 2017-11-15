package org.snapscript.studio.common;

import org.simpleframework.http.Path;
import org.snapscript.agent.log.ProcessLogger;

public interface FileDirectorySource {
   FileDirectory getProject(Path path);
   ProcessLogger getLogger();
}
