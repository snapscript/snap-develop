package org.snapscript.studio.project.generate;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;

import org.snapscript.studio.project.FileSystem;
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

   public synchronized <T extends ConfigFile> T getConfigFile(Project project, String path) {
      if(generators.isPresent() && cache.isEmpty()) {
         List<ConfigFileGenerator> list = generators.get();
         
         for(ConfigFileGenerator generator : list) {
            String configPath = generator.getConfigFilePath();
            cache.put(configPath, generator);
         }
      }
      ConfigFileGenerator generator = cache.get(path);
      FileSystem fileSystem = project.getFileSystem();
      
      if(generator != null) {
         File projectFile = fileSystem.getFile(PROJECT_FILE);
         String configPath = generator.getConfigFilePath();
         File configFile = fileSystem.getFile(configPath);
         
         try {
            if(!configFile.exists()) {
               ConfigFile file = generator.generateConfig(project);
               String source = file.getConfigSource();
               
               fileSystem.writeAsString(configPath, source);
               files.put(configPath, file);
            } else if(projectFile.exists()) {
               long projectFileChange = projectFile.lastModified();
               long configFileChange = configFile.lastModified();
               
               if(projectFileChange > configFileChange) {
                  ConfigFile file = generator.generateConfig(project);
                  String source = file.getConfigSource();
                  
                  fileSystem.writeAsString(configPath, source);
                  files.put(configPath, file);
               }
            } 
            ConfigFile file = files.get(configPath);
            
            if(file == null) {
               String source = fileSystem.readAsString(configPath);
               ConfigFile parsedFile = generator.parseConfig(project, source);
            
               if(parsedFile != null) {
                  files.put(configPath, parsedFile);
               }
            }
         } catch(Exception e) {
            log.info("Could not generate configuration file " + configPath, e);
         }
      }
      return (T)files.get(path);
   }
}
