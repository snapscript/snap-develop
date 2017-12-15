package org.snapscript.studio.project.generate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.snapscript.studio.project.Project;
import org.snapscript.studio.project.config.DependencyFile;
import org.snapscript.studio.project.config.ProjectConfiguration;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClassPathFileGenerator implements ConfigFileGenerator {
   
   private static final String CLASSPATH_FILE = ProjectConfiguration.CLASSPATH_FILE;

   @Override
   public ClassPathConfigFile generateConfig(Project project) {
      StringBuilder builder = new StringBuilder();
      List<String> errors = new ArrayList<String>();
      
      try {
         List<DependencyFile> dependencies = project.getDependencies(true);
   
         builder.append(".");
         builder.append("\n");
         
         if(dependencies != null) {
            for(DependencyFile dependency : dependencies) {
               File file = dependency.getFile();
               String message = dependency.getMessage();
               
               if(message == null && file != null) {
                  String normal = file.getCanonicalPath();
                  builder.append(normal);
               } else if(message != null){
                  errors.add(message);
                  builder.append("#! ");
                  builder.append(message);
               }
               builder.append("\n");
            }
         }
      } catch(Exception e) {
         log.info("Could not create class path", e);
      }
      String path = builder.toString();
      return new ClassPathConfigFile(path, errors);
   }

   @Override
   public ClassPathConfigFile parseConfig(Project project, String content) {
      StringBuilder builder = new StringBuilder();
      List<String> errors = new ArrayList<String>();
      
      try {
         String[] lines = content.split("\\r?\\n");
         
         for(String line : lines) {
            String trimmed = line.trim();
            
            if(!line.isEmpty()) {
               if(line.startsWith("#!")) {
                  String message = line.substring(2);
                  String error = message.trim();
                  
                  errors.add(error);
               } else if(!line.startsWith("#")) {
                  builder.append(trimmed);
                  builder.append("\n");
               }
            }
         }
      } catch(Exception e) {
         log.info("Could not parse file " + CLASSPATH_FILE, e);
         return generateConfig(project);
      }
      String path = builder.toString();
      return new ClassPathConfigFile(path, errors);
   }
   
   @Override
   public String getConfigFilePath() {
      return CLASSPATH_FILE;
   }
}
