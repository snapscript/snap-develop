package org.snapscript.develop.find;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularExpressionEvaluator extends MatchEvaluator {
   
   public RegularExpressionEvaluator(String expression) {
      super(expression);
   }

   public RegularExpressionEvaluator(String expression, String background, String foreground, boolean bold) {
      super(expression, background, foreground, bold);
   }

   protected List<MatchPart> match(String line, String source, String expression, String token) {
//      Pattern total = Pattern.compile(".*(" + expression + ").*", Pattern.CASE_INSENSITIVE);
//      Matcher matcher = total.matcher(line);
//      
//      if(matcher.matches()) {
//         List<MatchPart> tokens = new ArrayList<MatchPart>();
//         String[] parts = source.split(token);
//         
//         
//         return tokens;
//      }
      return null;
   }
}
