package org.snapscript.develop.find.text;

import java.io.File;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode
public class TextMatchQuery {
   private final File path;
   private final String project;
   private final String pattern;
   private final String replace;
   private final String query;
   private final boolean caseSensitive; 
   private final boolean regularExpression; 
   private final boolean wholeWord;
   private final boolean enableReplace;
}

