package org.snapscript.develop.http.resource;

import java.io.IOException;
import java.io.InputStream;

public class FileResolver {

   private final FileManager manager;

   public FileResolver(FileManager manager) {
      this.manager = manager;
   }

   public InputStream resolveStream(String path) throws IOException {
      return manager.openInputStream(path);
   }
}
