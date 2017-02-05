package org.snapscript.develop.complete;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.core.PrimitivePromoter;
import org.snapscript.core.Type;
import org.snapscript.core.function.Function;
import org.snapscript.core.function.Parameter;
import org.snapscript.core.function.Signature;
import org.snapscript.core.property.Property;
import org.snapscript.develop.common.DefaultTypeLoader;
import org.snapscript.develop.common.ResourceTypeLoader;
import org.snapscript.develop.common.TypeNode;
import org.snapscript.develop.configuration.ConfigurationClassLoader;

public class CompletionTypeResolver {

   private final PrimitivePromoter promoter;
   private final ResourceTypeLoader compiler;
   private final DefaultTypeLoader loader;
   
   public CompletionTypeResolver(ConfigurationClassLoader loader, ProcessLogger logger) {
      this.compiler = new ResourceTypeLoader(loader, logger);
      this.loader = new DefaultTypeLoader(logger);
      this.promoter = new PrimitivePromoter();
   }
   
   public Map<String, TypeNode> resolveTypes(Completion state) {
      int line = state.getLine();
      File root = state.getRoot();
      String source = state.getSource();
      String resource = state.getResource();
      Map<String, TypeNode> stateTypes = state.getTypes();
      Map<String, TypeNode> defaultTypes = loader.loadTypes();
      Map<String, TypeNode> resourceTypes = compiler.compileSource(root, resource, source, line, true);

      stateTypes.putAll(defaultTypes);
      stateTypes.putAll(resourceTypes);
      
      return expandFunctions(state);
   }
   
   private Map<String, TypeNode> expandFunctions(Completion state) {
      Map<String, TypeNode> types = state.getTypes();
      Set<String> names = new HashSet<String>(types.keySet());
      
      for(String name : names) {
         TypeNode type = types.get(name);
         List<Function> functions = type.getFunctions();
         List<Property> properties = type.getProperties();
         
         for(Function function : functions) {
            String key = function.getName();
            Type constraint = function.getConstraint();
           
            if(constraint != null) {
               Signature signature = function.getSignature();
               List<Parameter> parameters = signature.getParameters();
               TypeNode match = resolveType(state, constraint);
               int count = parameters.size();
               
               if(match != null) {
                  types.put(name + "." + key + "(" + count + ")", match);
                  types.put(key + "(" + count +")", match);
               }
            }
         }
         for(Property property : properties) {
            String key = property.getName();
            Type constraint = property.getConstraint();
            
            if(constraint != null) {
               TypeNode match = resolveType(state, constraint);
               
               if(match != null) {
                  types.put(key, match);
                  types.put(name + "." + key, match);
               }
            }
         }
      }
      return types;
   }
   
   private TypeNode resolveType(Completion state, Type constraint) {
      Map<String, TypeNode> types = state.getTypes();
      String name = constraint.getName();
      TypeNode match = types.get(name);
   
      if(match == null) {
         Class real = constraint.getType();
         
         if(real != null) { 
            real = promoter.promote(real);
         }
         if(real != null) {
            String identifier = real.getSimpleName();
            match = types.get(identifier);
         }
         if(match == null) {
            match = new TypeNode(constraint, name);
            types.put(name, match);
         }
      }
      return match;
   }
}
