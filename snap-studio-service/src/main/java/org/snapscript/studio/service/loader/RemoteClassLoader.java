package org.snapscript.studio.service.loader;

import java.net.URL;
import java.net.URLClassLoader;

public class RemoteClassLoader extends URLClassLoader {

   private final String prefix;
   
   public RemoteClassLoader(URL[] path, ClassLoader parent, String prefix) {
      super(path, parent);
      this.prefix = prefix;
   }
   
   @Override
   public Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
      ClassLoader loader = getParent();
      
      if(name.startsWith(prefix)) { // it should be remote
         return super.loadClass(name, resolve);
      }
      return loader.loadClass(name);
   }
}