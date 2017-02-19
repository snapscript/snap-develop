/*
 * TreePathFormatter.java December 2016
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

package org.snapscript.develop.http.tree;

public class TreePathFormatter {

   public static String formatPath(String project, String expand) {
      String expandPath = null;
      
      if(expand != null) {
         expandPath = expand;

         if(expandPath.startsWith("/")) {
            expandPath = expandPath.substring(1); 
         } 
         if(expandPath.endsWith("/")) {
            int length = expandPath.length();
            expandPath = expand.substring(0, length - 1);
         }
         expandPath = TreeConstants.ROOT + project + "/" + expandPath;
      }
      return expandPath;
   }
}
