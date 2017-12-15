package org.snapscript.studio.index.classpath;

public interface ClassFile {
   ClassFileCategory getClassCategory();
   ClassFileType getClassType();
   String getAbsolutePath();
   String getResourceName();
   String getLocation();
   String getFullName();
   String getTypeName();   
   String getName();
   String getModule();
   Class loadClass();
   int getModifiers();
}
