package org.snapscript.develop.complete;

import static org.snapscript.develop.complete.HintToken.CLASS;
import static org.snapscript.develop.complete.HintToken.CONSTANT;
import static org.snapscript.develop.complete.HintToken.ENUMERATION;
import static org.snapscript.develop.complete.HintToken.FUNCTION;
import static org.snapscript.develop.complete.HintToken.MODULE;
import static org.snapscript.develop.complete.HintToken.STRING;
import static org.snapscript.develop.complete.HintToken.TOKEN;
import static org.snapscript.develop.complete.HintToken.TRAIT;
import static org.snapscript.develop.complete.HintToken.VARIABLE;

import java.util.List;

import org.snapscript.parse.Token;

public class HintTokenProcessor {
   
   public static String getType(List<Token> tokens, int i) {
      if(isClass(tokens, i)) {
         return  CLASS;
      } else if(isTrait(tokens, i)) {
         return  TRAIT;
      } else if(isEnum(tokens, i)) {
         return  ENUMERATION;
      } else if(isModule(tokens, i)) {
         return  MODULE;
      } else if(isVariable(tokens, i)) {
         return  VARIABLE;
      } else if(isConstant(tokens, i)) {
         return  CONSTANT;
      } else if(isFunction(tokens, i)) {
         return  FUNCTION;
      } else if(isString(tokens, i)) {
         return  STRING;
      } 
      return TOKEN;
   }
   
   private static boolean isClass(List<Token> tokens, int index) {
      if(index > 1) {
         String space = tokens.get(index-1).getValue().toString();
         String literal = tokens.get(index-2).getValue().toString();
         
         if(space.equals(" ") && literal.equals("class")) {
            return true;
         }
      }
      return false;
   }
   
   private static boolean isEnum(List<Token> tokens, int index) {
      if(index > 1) {
         String space = tokens.get(index-1).getValue().toString();
         String literal = tokens.get(index-2).getValue().toString();
         
         if(space.equals(" ") && literal.equals("enum")) {
            return true;
         }
      }
      return false;
   }
   
   private static boolean isTrait(List<Token> tokens, int index) {
      if(index > 1) {
         String space = tokens.get(index-1).getValue().toString();
         String literal = tokens.get(index-2).getValue().toString();
         
         if(space.equals(" ") && literal.equals("trait")) {
            return true;
         }
      }
      return false;
   }
   
   private static boolean isModule(List<Token> tokens, int index) {
      if(index > 1) {
         String space = tokens.get(index-1).getValue().toString();
         String literal = tokens.get(index-2).getValue().toString();
         
         if(space.equals(" ") && literal.equals("module")) {
            return true;
         }
      }
      return false;
   }
   
   private static boolean isVariable(List<Token> tokens, int index) {
      int length = tokens.size();
      
      if(index > 1) {
         String space = tokens.get(index-1).getValue().toString();
         String literal = tokens.get(index-2).getValue().toString();
         
         if(space.equals(" ") && literal.equals("var")) {
            return true;
         }
      }
      if(index > 1 && index + 1 < length) {
         String previous = tokens.get(index-1).getValue().toString();
         String next = tokens.get(index+1).getValue().toString();
         
         if((previous.equals("(") || previous.equals(",")) && (next.equals(",") || next.equals(":") || next.equals(")"))) {
            return true;
         }
      }
      return false;
   }
   
   private static boolean isConstant(List<Token> tokens, int index) {
      if(index > 1) {
         String space = tokens.get(index-1).getValue().toString();
         String literal = tokens.get(index-2).getValue().toString();
         
         if(space.equals(" ") && literal.equals("const")) {
            return true;
         }
      }
      return false;
   }
   
   private static boolean isFunction(List<Token> tokens, int index) {
      int length = tokens.size();
      
      if(index + 1  < length) {
         String brace = tokens.get(index+1).getValue().toString();
         
         if(brace.equals("(")) {
            return true;
         }
      }
      return false;
   }
   
   private static boolean isString(List<Token> tokens, int index) {
      int length = tokens.size();
      
      if(index + 1  < length) {
         String next = tokens.get(index+1).getValue().toString();
         
         if(next.equals("\"") || next.equals("\'")) {
            return true;
         }
      }
      return false;
   }
}