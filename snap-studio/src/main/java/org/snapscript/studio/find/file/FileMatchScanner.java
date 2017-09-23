package org.snapscript.studio.find.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.snapscript.studio.common.FilePatternMatcher;
import org.snapscript.studio.find.ExpressionResolver;
import org.snapscript.studio.find.MatchEvaluator;
import org.snapscript.studio.find.MatchType;
import org.snapscript.studio.find.PathBuilder;

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
            MatchEvaluator evaluator = MatchEvaluator.of(MatchType.LITERAL, textMatch, false);
            String replaceText = evaluator.match(resourcePath);
            FileMatch projectFile = new FileMatch(project, resourcePath, file, replaceText);
            filesFound.add(projectFile);
         }
      }
      Collections.sort(filesFound);
      return filesFound;
   }
}