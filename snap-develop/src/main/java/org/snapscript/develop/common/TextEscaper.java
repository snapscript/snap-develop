/*
 * TextEscaper.java December 2016
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

package org.snapscript.develop.common;

public class TextEscaper {

   public static String escape(byte[] array) throws Exception{
      return escape(array, 0, array.length);
   }
   
   public static String escape(byte[] array, int offset, int length) throws Exception {
      String text = new String(array, offset, length, "UTF-8");
      return escape(text);
   }
   
   public static String escape(String text) throws Exception {
      int length = text.length();

      if (length > 0) {
         StringBuilder builder = new StringBuilder();

         for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            
            if (c > 127 || c == '"' || c == '<' || c == '>' || c == '&') {
               builder.append("&#");
               builder.append((int)c);
               builder.append(';');
            } else {
               builder.append(c);
            }
         }
         return builder.toString();

      }
      return text;
   }
}
