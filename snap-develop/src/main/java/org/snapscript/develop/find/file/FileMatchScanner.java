

package org.snapscript.develop.find.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.snapscript.develop.common.FilePatternMatcher;
import org.snapscript.develop.find.ExpressionResolver;
import org.snapscript.develop.find.MatchEvaluator;
import org.snapscript.develop.find.PathBuilder;

public class FileMatchScanner {

   public List<FileMatch> findAllFiles(File directory, String project, String expression) throws Exception {
      String root = directory.getCanonicalPath();
      int length = root.length();
      
      if(root.endsWith("/")) {
         root = root.substring(0, length -1);
      }
      List<FileMatch> filesFound = new ArrayList<FileMatch>();
      PathBuilder builder = new PathBuilder(root);
      ExpressionResolver resolver = new ExpressionResolver(expression);
      FileExpressionFilter filter = new FileExpressionFilter(resolver, builder);
      
      List<File> list = FilePatternMatcher.scan(filter, directory);
      
      for(File file : list) {
         String resourcePath = builder.buildPath(file);
         String textMatch = resolver.match(resourcePath);
         
         if(textMatch != null) {
            MatchEvaluator evaluator = new MatchEvaluator(textMatch);
            String replaceText = evaluator.match(resourcePath, false);
            FileMatch projectFile = new FileMatch(project, resourcePath, file, replaceText);
            filesFound.add(projectFile);
         }
      }
      Collections.sort(filesFound);
      return filesFound;
   }
}
