package org.snapscript.studio.common;

import java.io.File;

import org.simpleframework.http.Path;
import org.slf4j.Logger;

public interface FileDirectorySource {
   FileDirectory getProject(Path path);
   Logger getLogger();
   File createFile(String name);
}
