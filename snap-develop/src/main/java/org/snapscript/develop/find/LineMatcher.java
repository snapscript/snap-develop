/*
 * LineMatcher.java December 2016
 *
 * Copyright (C) 2016, Niall Gallagher <niallg@users.sf.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

package org.snapscript.develop.find;

public class LineMatcher {
   
   private final StringBuilder builder;
   private final String background;
   private final String foreground;
   private final String expression;
   private final String token;
   private final boolean bold;
   
   public LineMatcher(String expression, String background, String foreground, boolean bold) {
      this.builder = new StringBuilder();
      this.token = expression.toLowerCase();
      this.background = background;
      this.foreground = foreground;
      this.expression = expression;
      this.bold = bold;
   }

   public String match(String line) {
      String source = line.toLowerCase();
      int index = source.indexOf(token);
      
      if(index >= 0) {
         int length = expression.length();
         int start = 0;
         
         while(index >= 0) {
            String begin = line.substring(start, index);
            String text = line.substring(index, index + length);
            
            builder.append(escape(begin));
            builder.append("<span style='background-color: ");
            builder.append(background);
            builder.append("; color: ");
            builder.append(foreground);
            builder.append("; font-weight: ");
            builder.append(bold ? "bold" : "normal");
            builder.append(";'>");
            builder.append(escape(text));
            builder.append("</span>");
            start = index + length;
            index = source.indexOf(token, start);
         }
         int last = line.length();
         String remainder = line.substring(start, last);
         builder.append(escape(remainder));
         String result = builder.toString();
         builder.setLength(0);
         return result;
      }
      return null;
   }
   
   private String escape(String token) {
      return token
            .replace("<", "&lt;")
            .replace(">", "&gt;");
   }
}
