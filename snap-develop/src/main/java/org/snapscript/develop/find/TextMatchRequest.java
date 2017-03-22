package org.snapscript.develop.find;

import lombok.Builder;
import lombok.Data;

import org.simpleframework.http.Path;

@Data
@Builder
public class TextMatchRequest {
   private Path path;
   private String pattern;
   private String query;
   private boolean caseSensitive; 
   private boolean regularExpression; 
   private boolean wholeWord;
}

