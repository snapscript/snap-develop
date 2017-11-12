package org.snapscript.studio.common;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FilePatternMatcher {

   public static List<File> scan(Pattern pattern, File directory) throws Exception {
      PatternFilter filter = new PatternFilter(pattern);
      return scan(filter, directory);
   }
   
   public static List<File> scan(FilenameFilter filter, File directory) throws Exception {
      List<File> files = new ArrayList<File>();
      
      if(directory.exists()) {
         File[] list = directory.listFiles();
         String normal = directory.getCanonicalPath();
         
         if(filter.accept(directory, normal)) {
            files.add(directory);
         } else {
            for(File entry : list) {
               normal = entry.getCanonicalPath();
               
               if(filter.accept(entry, normal)) {
                  if(entry.exists() && entry.isFile()) {
                     files.add(entry);
                  }
               }
               if(entry.isDirectory()) {
                  List<File> matches = scan(filter, entry);
                  
                  if(!matches.isEmpty()) {
                     files.addAll(matches);
                  }
               }
            }
         }
      }
      return files;
   }
   
   private static class PatternFilter implements FilenameFilter {
      
      private final Pattern pattern;
      
      public PatternFilter(Pattern pattern) {
         this.pattern = pattern;
      }

      @Override
      public boolean accept(File dir, String name) {
         return pattern.matcher(name).matches();
      }
      
   }
}