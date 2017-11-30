package org.snapscript.studio.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.Executor;

import org.snapscript.common.store.NotFoundException;
import org.snapscript.common.store.Store;
import org.snapscript.compile.StoreContext;
import org.snapscript.core.Context;
import org.snapscript.studio.common.DirectoryWatcher;
import org.snapscript.studio.common.FileDirectory;
import org.snapscript.studio.index.IndexDatabase;
import org.snapscript.studio.project.config.ClassPathExecutor;
import org.snapscript.studio.project.config.ConfigurationClassLoader;
import org.snapscript.studio.project.config.ConfigurationReader;
import org.snapscript.studio.project.config.DependencyFile;

public class Project implements FileDirectory {
   
   private final ConfigurationClassLoader classLoader;
   private final FileSystem fileSystem;
   private final ProjectContext context;
   private final Workspace workspace;
   private final String projectName;
   private final String projectDirectory;
   private final Store store;

   public Project(ConfigurationReader reader, Workspace workspace, String projectDirectory, String projectName) {
      this.context = new ProjectContext(reader, workspace, this);
      this.classLoader = new ConfigurationClassLoader(this);
      this.fileSystem = new FileSystem(this);
      this.store = new ProjectStore();
      this.projectDirectory = projectDirectory;
      this.projectName = projectName;
      this.workspace = workspace;
   }
   
   public Workspace getWorkspace(){
      return workspace;
   }
   
   public String getProjectDirectory() {
      return projectDirectory;
   }

   public String getProjectName() {
      return projectName;
   }

   public FileSystem getFileSystem() {
      return fileSystem;
   }
   
   public ClassLoader getClassLoader() {
      return classLoader.getClassLoader();
   }
   
   public long getModificationTime(){
      return DirectoryWatcher.lastModified(getSourcePath());
   }

   public File getSourcePath() {
      try {
         return workspace.createFile(projectName);
      } catch (Exception e) {
         throw new IllegalStateException("Could not get source path for '" + projectName + "'", e);
      }
   }

   public File getProjectPath() {
      try {
         return workspace.createFile(projectName);
      } catch (Exception e) {
         throw new IllegalStateException("Could not get project path for '" + projectName + "'", e);
      }
   }
   
   public Context getProjectContext() {
      Executor threadPool = workspace.getExecutor();
      try {
         ClassPathExecutor executor = new ClassPathExecutor(this, threadPool);
         return new StoreContext(store, executor);
      }catch(Exception e) {
         throw new IllegalStateException("Could not create context for '" + projectName + "'", e);
      }
   }
   
   public IndexDatabase getIndexDatabase(){
      return context.getIndexDatabase();
   }
   
   public boolean isLayoutPath(String resource) {
      return context.getLayout().isLayoutPath(resource);
   }
   
   public String getRealPath(String resource) {
      File path = getSourcePath();
      return context.getLayout().getRealPath(path, resource);
   }
   
   public String getScriptPath(String resource) {
      File path = getSourcePath();
      return context.getLayout().getDownloadPath(path, resource);
   }

   public List<DependencyFile> getDependencies() {
      return context.getDependencies();
   }
   
   public ClassPathFile getClassPath() {
      return context.getClassPath();
   }
   
   private class ProjectStore implements Store {
   
      @Override
      public InputStream getInputStream(String path) {
         try {
            ProjectLayout layout = context.getLayout();
            File rootPath = getSourcePath();
            String projectPath = layout.getRealPath(rootPath, path);
            File realFile = fileSystem.getFile(projectPath);
            return new FileInputStream(realFile);
         } catch(Exception e) {
            throw new NotFoundException("Could not get source path for '" + path + "'", e);
         }
      }
   
      @Override
      public OutputStream getOutputStream(String path) {
         return System.out;
      }
   }
}