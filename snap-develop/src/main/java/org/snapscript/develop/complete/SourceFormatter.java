package org.snapscript.develop.complete;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.snapscript.develop.resource.project.Project;

public class SourceFormatter {

   private final Pattern pattern;
   
   public SourceFormatter(){
      this.pattern = Pattern.compile("^(\\s+)(.*)$");
   }
   
   public String format(Project project, String source, int indent) throws Exception {
      String lines[] = source.split("\\r?\\n");
      String pad = "";
      
      for(int i = 0; i < indent; i++) {
         pad += " ";
      }
      if(lines.length > 0){
         StringBuilder builder = new StringBuilder();
         
         for(String line : lines) {
            Matcher matcher = pattern.matcher(line);
            
            if(matcher.matches()) {
               String prefix = matcher.group(1);
               String remainder = matcher.group(2);
               float length = prefix.length();
               float factor = length / indent;
               
               if(length > 0) {
                  int count = Math.round(factor);
                  
                  for(int i = 0; i < count; i++) {
                     builder.append(pad);
                  }
                  builder.append(remainder);
               } else {
                  builder.append(remainder);
               }
            }else {
               builder.append(line);
            }
            builder.append("\n");
         }
         return builder.toString();
      }
      return source;
   }
}