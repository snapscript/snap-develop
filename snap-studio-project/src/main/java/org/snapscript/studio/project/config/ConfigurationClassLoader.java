package org.snapscript.studio.project.config;

import static org.snapscript.studio.project.config.ProjectConfiguration.CLASSPATH_FILE;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.snapscript.studio.agent.ClassPathUpdater;
import org.snapscript.studio.project.Project;
import org.snapscript.studio.project.ProjectFileSystem;

public class ConfigurationClassLoader {
   
   private final AtomicReference<ClassLoader> reference;
   private final AtomicLong lastUpdate;
   private final Project project;
   
   public ConfigurationClassLoader(Project project) {
      this.reference = new AtomicReference<ClassLoader>();
      this.lastUpdate = new AtomicLong();
      this.project = project;
   }
   
   public ClassLoader getClassLoader() {
      String name = project.getProjectName();
      long time = System.currentTimeMillis();
      
      try {
         if(isClassLoaderStale()) {
            String classPath = project.getClassPath();
            ClassLoader classLoader = createClassLoader(classPath);
            
            lastUpdate.set(time);
            reference.set(classLoader);
         }
      } catch(Exception e) {
         throw new IllegalStateException("Could not create class loader for project '" +name +"'", e);
      }
      return reference.get();
   }
   
   private boolean isClassLoaderStale() {
      ClassLoader classLoader = reference.get();
   
      if(classLoader != null) {
         ProjectFileSystem fileSystem = project.getFileSystem();
         File classPathFile = fileSystem.getFile(CLASSPATH_FILE);
         long lastModified = classPathFile.lastModified();
         long updateTime = lastUpdate.get();
         
         return classPathFile.exists() && updateTime < lastModified;
      }
      return true;
   }
   
   private ClassLoader createClassLoader(String dependencies) {
      try {
         List<File> files = ClassPathUpdater.parseClassPath(dependencies);
         File workspaceRoot = project.getWorkspace().getRoot();
         File tempPath = new File(workspaceRoot, WorkspaceConfiguration.TEMP_PATH);
         File agentFile = new File(tempPath, WorkspaceConfiguration.JAR_FILE);
         List<URL> locations = new ArrayList<URL>();
         
         if(agentFile.exists()) {
            files.add(agentFile);
         }
         URL[] array = new URL[]{};
         
         for(File file : files) {
            URL location = file.toURI().toURL();
            locations.add(location);
         }
         return new URLClassLoader(
               locations.toArray(array),
               ClassLoader.getSystemClassLoader().getParent());
      } catch(Exception e) {
         throw new IllegalStateException("Could not create project class loader", e);
      }
   }
}