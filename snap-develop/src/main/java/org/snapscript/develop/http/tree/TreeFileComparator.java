/*
 * TreeFileComparator.java December 2016
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

import java.io.File;
import java.util.Comparator;

public class TreeFileComparator implements Comparator<File>{

   @Override
   public int compare(File left, File right) {
      String leftPath = left.getAbsolutePath();
      String rightPath = right.getAbsolutePath();
      
      if(left.isDirectory() != right.isDirectory()) {
         if(left.isDirectory()) {
            return -1;
         }
         if(right.isDirectory()) {
            return 1;
         }
      }
      return leftPath.compareTo(rightPath);
   }

}
