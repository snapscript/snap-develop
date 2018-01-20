package org.snapscript.studio.project.generate;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;

import org.snapscript.studio.project.FileSystem;
import org.snapscript.studio.project.FilePersister;
import org.snapscript.studio.project.Project;
import org.snapscript.studio.project.config.ProjectConfiguration;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConfigFileSource {
   
   private static final String PROJECT_FILE = ProjectConfiguration.PROJECT_FILE;
   
   private final Optional<List<ConfigFileGenerator>> generators;
   private final Map<String, ConfigFileGenerator> cache;
   private final Map<String, ConfigFile> files;
   
   public ConfigFileSource(Optional<List<ConfigFileGenerator>> generators) {
      this.cache = new ConcurrentHashMap<String, ConfigFileGenerator>();
      this.files = new ConcurrentHashMap<String, ConfigFile>();
      this.generators = generators;
   }

   public synchronized <T extends ConfigFile> T getConfigFile(Project project, String name) {
      if(generators.isPresent() && cache.isEmpty()) {
         List<ConfigFileGenerator> list = generators.get();
         
         for(ConfigFileGenerator generator : list) {
            String configPath = generator.getConfigName(project);
            cache.put(configPath, generator);
         }
      }
      ConfigFileGenerator generator = cache.get(name);
      FileSystem fileSystem = project.getFileSystem();
      
      if(generator != null) {
         File projectFile = fileSystem.getFile(PROJECT_FILE);
         File configFile = generator.getConfigFilePath(project);
         
         try {
            String configKey = configFile.getCanonicalPath();
            
            if(!configFile.exists()) {
               ConfigFile file = generator.generateConfig(project);
               String source = file.getConfigSource();
               
               FilePersister.writeAsString(configFile, source);
               files.put(configKey, file);
            } else if(projectFile.exists()) {
               long projectFileChange = projectFile.lastModified();
               long configFileChange = configFile.lastModified();
               
               if(projectFileChange > configFileChange) {
                  ConfigFile file = generator.generateConfig(project);
                  String source = file.getConfigSource();
                  
                  FilePersister.writeAsString(configFile, source);
                  files.put(configKey, file);
               }
            } 
            ConfigFile file = files.get(configKey);
            
            if(file == null) {
               String source = FilePersister.readAsString(configFile);
               ConfigFile parsedFile = generator.parseConfig(project, source);
            
               if(parsedFile != null) {
                  files.put(configKey, parsedFile);
               }
            }
            return (T)files.get(configKey);
         } catch(Exception e) {
            log.info("Could not generate configuration file " + configFile, e);
         }
      }
      return null;
   }
}
