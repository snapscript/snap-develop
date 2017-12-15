package org.snapscript.studio.project.generate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.snapscript.studio.index.classpath.ClassFile;
import org.snapscript.studio.index.classpath.ClassFileMarshaller;
import org.snapscript.studio.index.scan.ClassPathScanner;
import org.snapscript.studio.project.Project;
import org.snapscript.studio.project.config.ProjectConfiguration;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Slf4j
@Component
public class SearchIndexFileGenerator implements ConfigFileGenerator {
   
   private static final String SEARCH_INDEX_FILE = ProjectConfiguration.INDEX_FILE;
   
   private final Gson gson;
   
   public SearchIndexFileGenerator() {
      this.gson = new GsonBuilder().setPrettyPrinting().create();
   }

   @Override
   public SearchIndexConfigFile generateConfig(Project project) {
      StringBuilder builder = new StringBuilder();
      List<ClassFile> files = new ArrayList<ClassFile>();
      ClassLoader loader = project.getClassLoader();
      
      try {
         List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
         files = ClassPathScanner.scanAllClasses(loader);
         
         for(ClassFile file : files) {
            try {
               Map<String, String> data = ClassFileMarshaller.toAttributes(file);
               dataList.add(data);
            }catch(Throwable e) {
               log.info("Could not load file", e);
            }
         }
         String text = gson.toJson(dataList);
         builder.append(text);
      } catch(Exception e) {
         return null;
      }
      String text = builder.toString();
      return new SearchIndexConfigFile(loader, files, text);
   }

   @Override
   public SearchIndexConfigFile parseConfig(Project project, String content) {
      StringBuilder builder = new StringBuilder();
      List<ClassFile> files = new ArrayList<ClassFile>();
      ClassLoader loader = project.getClassLoader();
      
      try {
         List<Map<String, String>> types = gson.fromJson(content, List.class);
         
         for(Map<String, String> type : types) {
            ClassFile file = ClassFileMarshaller.fromAttributes(type, loader);
            files.add(file);
         }
         builder.append(content);
      } catch(Exception e) {
         log.info("Could not parse search index file " + SEARCH_INDEX_FILE, e);
         return generateConfig(project);
      }
      String text = builder.toString();
      return new SearchIndexConfigFile(loader, files, text);
   }

   @Override
   public String getConfigFilePath() {
      return SEARCH_INDEX_FILE;
   }

}
