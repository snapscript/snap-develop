package org.snapscript.studio.index.classpath;

import java.net.URL;

public interface ClassFile {
   URL getURL();
   String getAbsolutePath();
   String getResourceName();
   String getLocation();
   String getFullName();
   String getTypeName();   
   String getName();
   String getModule();
   Class loadClass();
}
