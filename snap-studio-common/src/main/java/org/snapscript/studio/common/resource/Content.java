package org.snapscript.studio.common.resource;

import java.io.InputStream;
import java.io.Reader;

public interface Content {
   String getPath();
   Reader getReader();
   InputStream getInputStream();
   long getModificationTime();
   boolean isLocalFile();
   
}