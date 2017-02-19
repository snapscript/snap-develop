/*
 * SyntaxPrinter.java December 2016
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.snapscript.parse.SyntaxNode;
import org.snapscript.parse.SyntaxParser;
import org.snapscript.parse.Token;

public class SyntaxPrinter {
  
   public static String print(SyntaxParser analyzer, String source, String name) throws Exception {
      SyntaxNode node=analyzer.parse(null, source, name);
      StringWriter writer = new StringWriter();
      PrintWriter printer = new PrintWriter(writer);
      print(printer, node, 0, 0);
      printer.close();
      return writer.toString();
   }
   
   private static void print(PrintWriter builder, SyntaxNode token, int parent, int depth) throws Exception{ 
      String grammar = token.getGrammar();
      List<SyntaxNode> children = token.getNodes();
      Token t = token.getToken();
      
      for (int i = 0; i < depth; i++) {
         builder.print(" ");
      }      
      builder.printf("%s --> (", grammar);
      int count = 0;
      
      for(SyntaxNode next : children) { 
         String child = next.getGrammar();
         
         if(count++ != 0) {
            builder.print(", ");
         }
         builder.printf(child);
      }
      builder.print(")");
      builder.print(" = <");
      builder.print(token.getLine().getSource().trim());
      builder.print("> at line ");
      builder.print(token.getLine().getNumber()); 
      builder.println();
      builder.flush();
      
      for(SyntaxNode next : children) {   
         String child = next.getGrammar();
         
         
         //if(top.contains(child)) {
         if(child.equals(grammar)) {
            print(builder, next, System.identityHashCode(token), depth); // no depth change with no grammar change
         } else {
            print(builder, next, System.identityHashCode(token), depth+2);
         }
         //}
        // }else {
            //builder.println(next + " "+child.hasNext() + " "+iterator.ready());
          //  printStructure(child, top, depth); // stay at same depth
         //}
      }
   }  

}
