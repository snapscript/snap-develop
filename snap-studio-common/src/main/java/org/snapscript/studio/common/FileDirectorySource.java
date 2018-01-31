package org.snapscript.studio.common;

import java.io.File;

import org.simpleframework.http.Path;

public interface FileDirectorySource {
   FileDirectory getProject(String path);
   FileDirectory getProject(Path path);
   File createFile(String name);
}
