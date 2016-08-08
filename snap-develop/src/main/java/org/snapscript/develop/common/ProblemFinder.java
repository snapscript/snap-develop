package org.snapscript.develop.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.snapscript.core.Reserved;
import org.snapscript.parse.SyntaxCompiler;
import org.snapscript.parse.SyntaxNode;
import org.snapscript.parse.SyntaxParser;

public class ProblemFinder {

   private final SyntaxCompiler compiler;
   
   public ProblemFinder() {
      this.compiler = new SyntaxCompiler();
   }
   
   public Problem parse(String project, String resource, String source) {
      try {
         String name = resource.toLowerCase();
         
         if(name.endsWith(Reserved.SCRIPT_EXTENSION)) {
            SyntaxParser parser = compiler.compile();
            SyntaxNode node = parser.parse(resource, source, "script");
            node.getNodes();
         }
      }catch(Exception cause) {
         String message = cause.getMessage();
         Pattern pattern = Pattern.compile(".*line\\s+(\\d+)");
         Matcher matcher = pattern.matcher(message);
         
         if(matcher.matches()) {
            String match = matcher.group(1);
            int line = Integer.parseInt(match);
            
            return new Problem(project, resource, message, line);
         }
         return new Problem(project, resource, message, 1);
      }
      return null;
   }
}
