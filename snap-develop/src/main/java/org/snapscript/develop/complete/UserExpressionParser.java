
package org.snapscript.develop.complete;

import static org.snapscript.develop.complete.HintToken.CLASS;
import static org.snapscript.develop.complete.HintToken.CONSTANT;
import static org.snapscript.develop.complete.HintToken.ENUMERATION;
import static org.snapscript.develop.complete.HintToken.FUNCTION;
import static org.snapscript.develop.complete.HintToken.MODULE;
import static org.snapscript.develop.complete.HintToken.TOKEN;
import static org.snapscript.develop.complete.HintToken.TRAIT;
import static org.snapscript.develop.complete.HintToken.VARIABLE;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.develop.common.TypeNode;

public class UserExpressionParser {

   private final ProcessLogger logger;
   
   public UserExpressionParser(ProcessLogger logger) {
      this.logger = logger;
   }

   public UserExpression parse(Completion event, SourceContext context) {
      Set<String> tokens = new HashSet<String>();
      Map<String, TypeNode> types = event.getTypes();
      String complete = event.getComplete();
      String trim = complete.trim();
      
      if(trim.endsWith(".")) {
         tokens.add(FUNCTION);
         tokens.add(CONSTANT);
         tokens.add(VARIABLE);
      } else {
         if(complete.matches(".*new\\s+")) {
            tokens.add(CLASS);
         } else if(complete.matches(".*\\s+extends\\s+")) {
            tokens.add(TRAIT);
            tokens.add(CLASS);
         } else if(complete.matches(".*\\s+with\\s+")) {
            tokens.add(TRAIT);
         } else if(complete.matches(".*module\\s+")) {
            tokens.add(MODULE);
         } else {
            tokens.add(FUNCTION);
            tokens.add(CONSTANT);
            tokens.add(VARIABLE);
            tokens.add(MODULE);
            tokens.add(TRAIT);
            tokens.add(CLASS);
            tokens.add(ENUMERATION);
            tokens.add(TOKEN);
         }
      }
      TypeNode constraint = parseHint(types, complete);
      
      if(constraint != null) {
         logger.debug("'" + complete + "' is constrained to " + constraint);
      } else {
         logger.debug("'" + complete + "' has no known constraints");
      }
      return new UserExpression(complete, constraint, context, tokens);
   }
   
   private TypeNode parseHint(Map<String, TypeNode> types, String complete) {
      ConstraintHint[] hints = ConstraintHint.values();
      
      for(ConstraintHint hint : hints) {
         String constraint = hint.parse(complete);
         
         if(constraint != null) {
            TypeNode type = types.get(constraint);
            
            if(type != null){
               logger.info("match was " + constraint);
               return type;
            }
         }
      }
      return null;
   }
   
   private static enum ConstraintHint {
      CONSTRUCTOR_WITH_PARAMS("new\\s+([a-zA-Z0-9_]+)\\(([a-zA-Z0-9_,\\s]+)\\)\\.$"){
         @Override
         public String parse(String complete) {
            Matcher matcher = pattern.matcher(complete);
            
            if(matcher.matches()) {
               return matcher.group(1);
            }
            return null;
         }
      },
      CONSTRUCTOR("new\\s+([a-zA-Z0-9_]+)\\(\\)\\.$"){
         @Override
         public String parse(String complete) {
            Matcher matcher = pattern.matcher(complete);
            
            if(matcher.matches()) {
               return matcher.group(1);
            }
            return null;
         }
      },
      CONSTRUCTOR_WITH_PARAMS_PROPERTY("new\\s+([a-zA-Z0-9_]+)\\(([a-zA-Z0-9_,\\s]+)\\)\\.([a-zA-Z0-9_,\\s]+)\\.$"){
         @Override
         public String parse(String complete) {
            Matcher matcher = pattern.matcher(complete);
            
            if(matcher.matches()) {
               String type = matcher.group(1);
               String property = matcher.group(3);
               
               return String.format("%s.%s", type, property);
            }
            return null;
         }
      },
      CONSTRUCTOR_WITH_PROPERTY("new\\s+([a-zA-Z0-9_]+)\\(\\)\\.([a-zA-Z0-9_,\\s]+)\\.$"){
         @Override
         public String parse(String complete) {
            Matcher matcher = pattern.matcher(complete);
            
            if(matcher.matches()) {
               String type = matcher.group(1);
               String property = matcher.group(2);
               
               return String.format("%s.%s", type, property);
            }
            return null;
         }
      },
      CONSTRUCTOR_WITH_PARAMS_FUNCTION_WITH_PARAMS("new\\s+([a-zA-Z0-9_]+)\\(([a-zA-Z0-9_,\\s]+)\\)\\.([a-zA-Z0-9_]+)\\(([a-zA-Z0-9_,\\s]+)\\)\\.$"){
         @Override
         public String parse(String complete) {
            Matcher matcher = pattern.matcher(complete);
            
            if(matcher.matches()) {
               String type = matcher.group(1);
               String function = matcher.group(3);
               String parameters = matcher.group(4);
               String[] names = parameters.split(",");
               String invocation = String.format("%s(%s)", function, names.length);
               
               return String.format("%s.%s", type, invocation);
            }
            return null;
         }
      },
      CONSTRUCTOR_WITH_PARAMS_FUNCTION("new\\s+([a-zA-Z0-9_]+)\\(([a-zA-Z0-9_,\\s]+)\\)\\.([a-zA-Z0-9_]+)\\(\\)\\.$"){
         @Override
         public String parse(String complete) {
            Matcher matcher = pattern.matcher(complete);
            
            if(matcher.matches()) {
               String type = matcher.group(1);
               String invocation = matcher.group(3);
               
               return String.format("%s.%s", type, invocation);
            }
            return null;
         }
      },
      CONSTRUCTOR_WITH_FUNCTION_WITH_PARAMS("new\\s+([a-zA-Z0-9_]+)\\(\\)\\.([a-zA-Z0-9_]+)\\(([a-zA-Z0-9_,\\s]+)\\)\\.$"){
         @Override
         public String parse(String complete) {
            Matcher matcher = pattern.matcher(complete);
            
            if(matcher.matches()) {
               String type = matcher.group(1);
               String function = matcher.group(2);
               String parameters = matcher.group(3);
               String[] names = parameters.split(",");
               String invocation = String.format("%s(%s)", function, names.length);
               
               return String.format("%s.%s", type, invocation);
            }
            return null;
         }
      },
      CONSTRUCTOR_WITH_FUNCTION("new\\s+([a-zA-Z0-9_]+)\\(\\)\\.([a-zA-Z0-9_]+)\\(\\)\\.$"){
         @Override
         public String parse(String complete) {
            Matcher matcher = pattern.matcher(complete);
            
            if(matcher.matches()) {
               String type = matcher.group(1);
               String invocation = matcher.group(2);
               
               return String.format("%s.%s(0)", type, invocation);
            }
            return null;
         }
      },
      TYPE_WITH_FUNCTION_WITH_PARAMS("([a-zA-Z0-9_]+)\\.([a-zA-Z0-9_]+)\\(([a-zA-Z0-9_,\\s]+)\\)\\.$"){
         @Override
         public String parse(String complete) {
            Matcher matcher = pattern.matcher(complete);
            
            if(matcher.matches()) {
               String type = matcher.group(1);
               String function = matcher.group(2);
               String parameters = matcher.group(3);
               String[] names = parameters.split(",");
               String invocation = String.format("%s(%s)", function, names.length);
               
               return String.format("%s.%s", type, invocation);
            }
            return null;
         }
      },
      TYPE_WITH_FUNCTION("([a-zA-Z0-9_]+)\\.([a-zA-Z0-9_]+)\\(\\)\\.$"){
         @Override
         public String parse(String complete) {
            Matcher matcher = pattern.matcher(complete);
            
            if(matcher.matches()) {
               String type = matcher.group(1);
               String function = matcher.group(2);
               String invocation = String.format("%s(0)", function);
               
               return String.format("%s.%s", type, invocation);
            }
            return null;
         }
      },
      TYPE_WITH_PROPERTY("([a-zA-Z0-9_]+)\\.([a-zA-Z0-9_]+)\\.$"){
         @Override
         public String parse(String complete) {
            Matcher matcher = pattern.matcher(complete);
            
            if(matcher.matches()) {
               String type = matcher.group(1);
               String property = matcher.group(2);
               
               return String.format("%s.%s", type, property);
            }
            return null;
         }
      },
      FUNCTION_WITH_PARAMS("([a-zA-Z0-9_]+)\\(([a-zA-Z0-9_,\\s]+)\\)\\.$"){
         @Override
         public String parse(String complete) {
            Matcher matcher = pattern.matcher(complete);
            
            if(matcher.matches()) {
               String function = matcher.group(1);
               String parameters = matcher.group(2);
               String[] names = parameters.split(",");
               
               return String.format("%s(%s)", function, names.length);
            }
            return null;
         }
      },
      FUNCTION("([a-zA-Z0-9_]+)\\(\\)\\.$"){
         @Override
         public String parse(String complete) {
            Matcher matcher = pattern.matcher(complete);
            
            if(matcher.matches()) {
               String function = matcher.group(1);
               return String.format("%s(0)", function);
            }
            return null;
         }
      },
      TYPE("([a-zA-Z0-9_]+)\\.$"){
         @Override
         public String parse(String complete) {
            Matcher matcher = pattern.matcher(complete);
            
            if(matcher.matches()) {
               return matcher.group(1);
            }
            return null;
         }
      },
      PROPERTY("([a-zA-Z0-9_]+)\\.$"){
         @Override
         public String parse(String complete) {
            Matcher matcher = pattern.matcher(complete);
            
            if(matcher.matches()) {
               return matcher.group(1);
            }
            return null;
         }
      };
      
      public final Pattern pattern;
      
      private ConstraintHint(String pattern) {
         this.pattern = Pattern.compile(".*?" + pattern);
      }
      
      public abstract String parse(String complete);
   }
}
