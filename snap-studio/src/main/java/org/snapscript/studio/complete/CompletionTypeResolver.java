package org.snapscript.studio.complete;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.snapscript.core.Context;
import org.snapscript.core.PrimitivePromoter;
import org.snapscript.core.Type;
import org.snapscript.core.function.Function;
import org.snapscript.core.function.Parameter;
import org.snapscript.core.function.Signature;
import org.snapscript.core.property.Property;
import org.snapscript.studio.Workspace;
import org.snapscript.studio.common.DefaultTypeLoader;
import org.snapscript.studio.common.ResourceTypeLoader;
import org.snapscript.studio.common.TypeNode;

public class CompletionTypeResolver {

   private final PrimitivePromoter promoter;
   private final ResourceTypeLoader compiler;
   private final DefaultTypeLoader loader;
   
   public CompletionTypeResolver(Workspace workspace) {
      this.compiler = new ResourceTypeLoader(workspace);
      this.loader = new DefaultTypeLoader(workspace.getLogger());
      this.promoter = new PrimitivePromoter();
   }
   
   public Map<String, TypeNode> resolveTypes(Completion state) {
      int line = state.getLine();
      String project = state.getProjectName();
      String source = state.getSource();
      String resource = state.getResource();
      Map<String, TypeNode> stateTypes = state.getTypes();
      Map<String, TypeNode> defaultTypes = loader.loadTypes();
      Map<String, TypeNode> resourceTypes = compiler.compileSource(project, resource, source, line, true);

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
      Context context = constraint.getModule().getContext();
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
            match = TypeNode.createNode(context, constraint, name);
            types.put(name, match);
         }
      }
      return match;
   }
}