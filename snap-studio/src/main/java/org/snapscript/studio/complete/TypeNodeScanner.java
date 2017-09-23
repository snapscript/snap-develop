package org.snapscript.studio.complete;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.simpleframework.http.Path;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.common.thread.ThreadPool;
import org.snapscript.core.Reserved;
import org.snapscript.core.function.Function;
import org.snapscript.core.function.Parameter;
import org.snapscript.core.function.Signature;
import org.snapscript.core.property.Property;
import org.snapscript.studio.common.FileAction;
import org.snapscript.studio.common.FileProcessor;
import org.snapscript.studio.common.FileReader;
import org.snapscript.studio.common.TypeNode;
import org.snapscript.studio.common.TypeNodeFinder;
import org.snapscript.studio.configuration.ConfigurationClassLoader;
import org.snapscript.studio.resource.project.Project;
import org.snapscript.studio.resource.project.ProjectBuilder;

public class TypeNodeScanner {

   private final FileProcessor<Map<String, TypeNode>> processor;
   private final FileAction<Map<String, TypeNode>> action;
   private final ProjectBuilder builder;
   private final ProcessLogger logger;
   
   public TypeNodeScanner(ProjectBuilder builder, ConfigurationClassLoader loader, ProcessLogger logger, ThreadPool pool) {
      this.action = new CompileAction(builder, loader, logger);
      this.processor = new FileProcessor<Map<String, TypeNode>>(action, pool);
      this.builder = builder;
      this.logger = logger;
   }
   
   public Map<String, TypeNodeReference> findTypes(Path path, String expression) throws Exception {
      Project project = builder.createProject(path);
      String name = project.getProjectName();
      File directory = project.getProjectPath();
      String root = directory.getCanonicalPath();
      long start = System.currentTimeMillis();
      int length = root.length();
      
      if(root.endsWith("/")) {
         root = root.substring(0, length -1);
      }
      Map<String, TypeNodeReference> typeNodes = new HashMap<String, TypeNodeReference>();
      
      try {
         Set<Map<String, TypeNode>> resourceTypes = processor.process(name, root + "/**.snap"); // build all resources
      
         for(Map<String, TypeNode> types : resourceTypes) {
            Set<String> typeNames = types.keySet();
            
            for(String typeName : typeNames) {
               TypeNode typeNode = types.get(typeName);
               String typePath = typeNode.getResource();
               List<Function> typeFunctions = typeNode.getFunctions(false);
               List<Property> typeProperties = typeNode.getProperties(false);
               
               if(typeName.matches(expression)) {
                  Map<String, Set<Integer>> functionNames = new LinkedHashMap<String, Set<Integer>>();
                  Set<String> propertyNames = new LinkedHashSet<String>();
                  TypeNodeReference reference = null;
                  
                  for(Function typeFunction : typeFunctions) {
                     String functionName = typeFunction.getName();
                     Signature functionSignature = typeFunction.getSignature();
                     List<Parameter> functionParams = functionSignature.getParameters();
                     Set<Integer> functionParameterCounts = functionNames.get(functionName);
                     int functionParamCount = functionParams.size();
                     
                     if(functionParameterCounts == null) {
                        functionParameterCounts = new LinkedHashSet<Integer>();
                        functionNames.put(functionName, functionParameterCounts);
                     }
                     if(Reserved.TYPE_CONSTRUCTOR.equals(functionName)) {
                        functionParameterCounts.add(functionParamCount - 1);
                     }else {
                        functionParameterCounts.add(functionParamCount);
                     }
                  }
                  for(Property typeProperty : typeProperties) {
                     String propertyName = typeProperty.getName();
                     propertyNames.add(propertyName);
                  }
                  if(typeNode.isModule()) {
                     reference = new TypeNodeReference(functionNames, propertyNames, typeName, typePath, TypeNodeReference.MODULE);
                  } else {
                     reference = new TypeNodeReference(functionNames, propertyNames, typeName, typePath, TypeNodeReference.CLASS);
                  }
                  typeNodes.put(typeName +":" + typePath, reference);
               }
            }
         }
         return typeNodes;
      } finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         if(logger.isTrace()) {
            logger.trace("Took " + duration + " ms to compile project " + name);
         }
      }
   }
   
   private static class CompileAction implements FileAction<Map<String, TypeNode>> {
   
      private final ProjectBuilder builder;
      private final TypeNodeFinder finder;
      private final ProcessLogger logger;
      
      public CompileAction(ProjectBuilder builder, ConfigurationClassLoader loader, ProcessLogger logger) {
         this.finder = new TypeNodeFinder(loader, logger);
         this.builder = builder;
         this.logger = logger;
      }
      
      @Override
      public Map<String, TypeNode> execute(String reference, File file) throws Exception {
         Project project = builder.getProject(reference);
         String name = project.getProjectName();
         File root = project.getProjectPath();
         String rootPath = root.getCanonicalPath();
         String filePath = file.getCanonicalPath();
         String relativePath = filePath.replace(rootPath, "");
         String resourcePath = relativePath.replace(File.separatorChar, '/');
         
         if(!resourcePath.startsWith("/")) {
            resourcePath = "/" + resourcePath;
         }
         String source = FileReader.readText(file);
         
         if(logger.isTrace()) {
            logger.trace("Compiling " + resourcePath + " in project " + reference);
         }
         return finder.parse(root, name, resourcePath, source);
      }
   }
}