package org.snapscript.studio.project;

import static org.snapscript.studio.project.config.ProjectConfiguration.CLASSPATH_FILE;
import static org.snapscript.studio.project.config.ProjectConfiguration.PROJECT_FILE;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.snapscript.studio.agent.ClassPathUpdater;
import org.snapscript.studio.index.IndexDatabase;
import org.snapscript.studio.index.IndexScanner;
import org.snapscript.studio.index.config.IndexConfigFile;
import org.snapscript.studio.project.config.ConfigurationReader;
import org.snapscript.studio.project.config.Dependency;
import org.snapscript.studio.project.config.DependencyFile;
import org.snapscript.studio.project.config.ProjectConfiguration;
import org.snapscript.studio.project.generate.ConfigFileSource;

@Slf4j
public class ProjectContext {
   
   private static final String INDEX_DATABASE_KEY = "database";
   
   private final ConfigurationReader reader;
   private final ConfigFileSource source;
   private final Workspace workspace;
   private final Project project;
   
   public ProjectContext(ConfigurationReader reader, ConfigFileSource source, Workspace workspace, Project project) {
      this.workspace = workspace;
      this.project = project;
      this.source = source;
      this.reader = reader;
   }

   public synchronized ProjectConfiguration getConfiguration() {
      String projectName = project.getProjectName();
      
      try {
         return reader.loadProjectConfiguration(projectName);
      } catch (Exception e) {
         log.info("Could not read " + PROJECT_FILE + " file for '" + projectName + "'", e);
      }
      return null;
   }
   
   public synchronized ProjectLayout getLayout() {
      String projectName = project.getProjectName();
      
      try {
         return getConfiguration().getProjectLayout();
      } catch (Exception e) {
         log.info("Could not read " + PROJECT_FILE + " file for '" + projectName + "'", e);
      }
      return new ProjectLayout();
   }
   
   public synchronized IndexDatabase getIndexDatabase(){
      ProjectConfiguration configuraton = getConfiguration();
      IndexDatabase database = configuraton.getAttribute(INDEX_DATABASE_KEY);
      
      if(database == null) {
         database = new IndexScanner(
            (IndexConfigFile)source.getConfigFile(project, ProjectConfiguration.INDEX_FILE),
            project.getProjectContext(), 
            workspace.getExecutor(), 
            project.getProjectPath(), 
            project.getProjectName(), 
            getLayout().getPrefixes());

         configuraton.setAttribute(INDEX_DATABASE_KEY, database);
      }
      return database;
   }

   public synchronized List<DependencyFile> getDependencies() {
      return getDependencies(false);
   }
   
   public synchronized List<DependencyFile> getDependencies(boolean refresh) {
      List<DependencyFile> dependencies = new ArrayList<DependencyFile>();
      
      try {
         if(refresh) {
            return getDeclaredDependencies(); // force a maven lookup
         }
         ClassPathFile classPath = getClassPath();
         String content = classPath.getPath();
         List<File> files = ClassPathUpdater.parseClassPath(content);
         List<String> errors = classPath.getErrors();
         
         for(File file : files) {
            if(!file.exists()) {
               if(source.deleteConfigFile(project, CLASSPATH_FILE)) { // make sure we rewrite the file
                  String projectName = project.getProjectName();
                  String filePath = file.getAbsolutePath();

                  log.info("Deleting " + CLASSPATH_FILE + " from project " + projectName + " as " + filePath + " not found");
               }
               return getDeclaredDependencies(); // force a maven lookup
            }
            DependencyFile entry = new DependencyFile(file);
            dependencies.add(entry);
         }
         for(String error : errors) {
            DependencyFile entry = new DependencyFile(null, error);
            dependencies.add(entry);
         }
      } catch(Exception e) {
         throw new IllegalStateException(e.getMessage(), e);
      }
      return dependencies;
   }
   
   public synchronized ClassPathFile getClassPath() {
      return (ClassPathFile)source.getConfigFile(project, CLASSPATH_FILE);
   }
   
   private synchronized List<DependencyFile> getDeclaredDependencies(){ 
      try {
         ProjectConfiguration configuration = getConfiguration();
         List<Dependency> dependencies = configuration.getDependencies();
         
         if(!dependencies.isEmpty()) {
            return workspace.resolveDependencies(dependencies);
         }
         return Collections.emptyList();
      } catch(Exception e) {
         throw new IllegalStateException(e.getMessage(), e);
      }
   }
}