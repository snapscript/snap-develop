package org.snapscript.develop.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilePatternMatcher {

   public static List<File> scan(Pattern pattern, File directory) throws Exception {
      List<File> files = new ArrayList<File>();
      
      if(directory.exists()) {
         File[] list = directory.listFiles();
         String normal = directory.getCanonicalPath();
         Matcher matcher = pattern.matcher(normal);
         
         if(matcher.matches()) {
            files.add(directory);
         } else {
            for(File entry : list) {
               normal = entry.getCanonicalPath();
               matcher = pattern.matcher(normal);
               
               if(matcher.matches()) {
                  if(entry.exists() && entry.isFile()) {
                     files.add(entry);
                  }
               }
               if(entry.isDirectory()) {
                  List<File> matches = scan(pattern, entry);
                  
                  if(!matches.isEmpty()) {
                     files.addAll(matches);
                  }
               }
            }
         }
      }
      return files;
   }
}
