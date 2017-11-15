package org.snapscript.studio.complete;

import static org.snapscript.studio.complete.HintToken.CLASS;
import static org.snapscript.studio.complete.HintToken.CONSTANT;
import static org.snapscript.studio.complete.HintToken.ENUMERATION;
import static org.snapscript.studio.complete.HintToken.FUNCTION;
import static org.snapscript.studio.complete.HintToken.MODULE;
import static org.snapscript.studio.complete.HintToken.TRAIT;
import static org.snapscript.studio.complete.HintToken.VARIABLE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.snapscript.core.FilePathConverter;
import org.snapscript.core.ModifierType;
import org.snapscript.core.PathConverter;
import org.snapscript.core.Type;
import org.snapscript.core.function.Function;
import org.snapscript.core.function.Parameter;
import org.snapscript.core.function.Signature;
import org.snapscript.core.property.Property;
import org.snapscript.parse.GrammarIndexer;
import org.snapscript.parse.GrammarResolver;
import org.snapscript.studio.Workspace;
import org.snapscript.studio.search.TypeNode;

public class CompletionMatcher {
   
   private final SourceContextExtractor extractor;
   private final UserExpressionParser parser;
   private final CompletionTypeResolver resolver;
   private final PathConverter converter;
   private final Workspace workspace;
   
   public CompletionMatcher(GrammarResolver resolver, GrammarIndexer indexer, Workspace workspace) {
      this.extractor = new SourceContextExtractor(resolver, indexer);
      this.parser = new UserExpressionParser(workspace.getLogger());
      this.resolver = new CompletionTypeResolver(workspace);
      this.converter = new FilePathConverter();
      this.workspace = workspace;
   }
   
   public Map<String, String> findTokens(Completion state) {
      Map<String, String> resultTokens = state.getTokens();
      long start = System.currentTimeMillis();
      
      try {
         String resource = state.getResource();
         Map<String, TypeNode> types = resolver.resolveTypes(state);
         SourceContext context = extractor.extractContext(state);
         UserExpression expression = parser.parse(state, context);
         TypeNode type = expression.getConstraint();
         String module = converter.createModule(resource);
         
         if(type != null) {
            Map<String, String> availableTokens = extractTypeTokens(state, type, expression);
            resultTokens.putAll(availableTokens);
         }
         TypeNode thisType = context.getType();
         TypeNode thisModule = types.get(module);
         Map<String, String> thisTokens = context.getTokens();
         
         if(thisType != null) {
            Map<String, String> availableTokens = extractTypeTokens(state, thisType, expression);
            resultTokens.putAll(availableTokens);
         }
         if(thisModule != null) {
            Map<String, String> availableTokens = extractTypeTokens(state, thisModule, expression);
            resultTokens.putAll(availableTokens);
         }
         Map<String, String> globalTokens = extractGlobalTokens(state, expression);
         resultTokens.putAll(globalTokens);
         resultTokens.putAll(thisTokens);
         return resultTokens;
      } finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         workspace.getLogger().info("Time taken to find tokens " + duration);
      }
   }
   
   private Map<String, String> extractTypeTokens(Completion state, TypeNode type, UserExpression complete) {
      String prefix = state.getPrefix();
      Map<String, String> strings = new HashMap<String,String>();
      TokenFilter filter = new TokenFilter(complete, prefix);
      List<Function> functions = type.getFunctions();
      List<Property> properties = type.getProperties();
      
      for(Function function : functions) {
         String name = function.getName();
         
         if(filter.acceptToken(name, FUNCTION)) {
            Signature signature = function.getSignature();
            List<Parameter> parameters = signature.getParameters();
            Type constraint = function.getConstraint(); // perhaps add the return type
            int count = parameters.size();
            
            if(count > 0) {
               StringBuilder builder = new StringBuilder();
               
               for(int i = 0; i < count; i++) {
                  Parameter parameter = parameters.get(i);
                  String id = parameter.getName();
                  
                  if(i > 0) {
                     builder.append(", ");
                  }
                  builder.append(id);
               }
               strings.put(name + "(" + builder + ")", FUNCTION);
            } else {
               strings.put(name + "()", FUNCTION);
            }
         }
      }
      for(Property property : properties) {
         String name = property.getName();
         int modifiers = property.getModifiers();
         
         if(ModifierType.isConstant(modifiers)) {
            if(filter.acceptToken(name, CONSTANT)) {
               strings.put(name, CONSTANT);
            }
         } else {
            if(filter.acceptToken(name, VARIABLE)) {
               strings.put(name, VARIABLE);
            }
         }
      }
      return strings;
   }
   
   private Map<String, String> extractGlobalTokens(Completion state, UserExpression complete) {
      String prefix = state.getPrefix();
      Map<String, TypeNode> types = state.getTypes();
      Map<String, String> strings = new HashMap<String,String>();
      TokenFilter filter = new TokenFilter(complete, prefix);
      Set<String> names = types.keySet();
      
      for(String key : names) {
         TypeNode type = types.get(key);
         String name = type.getName();
         Class real = type.getType();
         
         if(real != null) {
            if(!real.isArray()) {
               if(real.isInterface()) {
                  if(filter.acceptToken(name, TRAIT)) {
                     strings.put(name, TRAIT);
                  }
               } else if(real.isEnum()){
                  if(filter.acceptToken(name, ENUMERATION)) {
                     strings.put(name, ENUMERATION);
                  }
               } else {
                  if(filter.acceptToken(name, CLASS)) {
                     strings.put(name, CLASS);
                  }
               }
            }
         } else {
            if(type.isType()) {
               if(filter.acceptToken(name, CLASS)) {
                  strings.put(name, CLASS);
               }
            } else if(type.isModule()) {
               if(filter.acceptToken(name, MODULE)) {
                  strings.put(name, MODULE);
               }
            }
         }
      }
      return strings;
   }
}