package org.snapscript.studio.complete;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.simpleframework.http.Path;
import org.snapscript.common.thread.ThreadPool;
import org.snapscript.core.Context;
import org.snapscript.core.Reserved;
import org.snapscript.core.function.Function;
import org.snapscript.core.function.Parameter;
import org.snapscript.core.function.Signature;
import org.snapscript.core.property.Property;
import org.snapscript.studio.Workspace;
import org.snapscript.studio.common.FileAction;
import org.snapscript.studio.common.FileProcessor;
import org.snapscript.studio.common.FileReader;
import org.snapscript.studio.common.TypeNode;
import org.snapscript.studio.common.TypeNodeFinder;
import org.snapscript.studio.resource.project.Project;
import org.snapscript.studio.resource.project.ProjectLayout;

import com.google.common.reflect.ClassPath.ClassInfo;

public class TypeNodeScanner {

   private final FileProcessor<Map<String, TypeNode>> processor;
   private final FileAction<Map<String, TypeNode>> action;
   private final Workspace workspace;
   
   public TypeNodeScanner(Workspace workspace, ThreadPool pool) {
      this.action = new CompileAction(workspace);
      this.processor = new FileProcessor<Map<String, TypeNode>>(action, pool);
      this.workspace = workspace;
   }
   
   public Map<String, TypeNodeReference> findTypesIncludingClasses(Path path, String expression) throws Exception {
      Map<String, TypeNodeReference> typeNodes = findTypes(path, expression);
      Project project = workspace.createProject(path);
      Set<ClassInfo> javaTypes = project.getAllClasses();
      Context context = project.getProjectContext();
      
      for(ClassInfo info : javaTypes) {
         String resourcePath = info.getResourceName();
         
         if(!resourcePath.startsWith("target/")) {
            String simpleName = info.getSimpleName();
            String typeName = info.getName();
         
            if(typeName.matches(expression) || simpleName.matches(expression)) {
               Class realType = info.load();
               
               simpleName = realType.getSimpleName();
               typeName = realType.getName();
               
               if(typeName.matches(expression) || simpleName.matches(expression)) {
                  TypeNode typeNode = TypeNode.createNode(context, realType, simpleName);
                  String typePath = typeNode.getResource();
                  TypeNodeReference reference = createTypeReference(typeNode, typePath, typePath);
                  
                  typeNodes.put(typeName +":" + typePath, reference);
               }
            }
         }
      }
      return typeNodes;
   }
   
   public Map<String, TypeNodeReference> findTypes(Path path, String expression) throws Exception {
      Map<String, TypeNodeReference> typeNodes = new HashMap<String, TypeNodeReference>();
      Project project = workspace.createProject(path);
      ProjectLayout layout = project.getLayout();
      String name = project.getProjectName();
      File directory = project.getProjectPath();
      String root = directory.getCanonicalPath();
      long start = System.currentTimeMillis();
      int length = root.length();
      
      if(root.endsWith("/")) {
         root = root.substring(0, length -1);
      }
      try {
         Set<Map<String, TypeNode>> resourceTypes = processor.process(name, root + "/**.snap"); // build all resources
         
         for(Map<String, TypeNode> types : resourceTypes) {
            Set<String> typeNames = types.keySet();
            
            for(String typeName : typeNames) {
               if(typeName.matches(expression)) {
                  TypeNode typeNode = types.get(typeName);
                  String typePath = typeNode.getResource();
                  String realPath = layout.getRealPath(directory, typePath);
                  TypeNodeReference reference = createTypeReference(typeNode, typePath, realPath);
                  
                  typeNodes.put(typeName +":" + typePath, reference);
               }
            }
         }
         return typeNodes;
      } finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         if(workspace.getLogger().isTrace()) {
            workspace.getLogger().trace("Took " + duration + " ms to compile project " + name);
         }
      }
   }

   private TypeNodeReference createTypeReference(TypeNode typeNode, String typePath, String realPath) {
      Map<String, Set<Integer>> functionNames = new LinkedHashMap<String, Set<Integer>>();
      String typeName = typeNode.getName();
      List<Function> typeFunctions = typeNode.getFunctions(false);
      List<Property> typeProperties = typeNode.getProperties(false);
      Set<String> propertyNames = new LinkedHashSet<String>();
      
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
         return new TypeNodeReference(functionNames, propertyNames, typeName, realPath, TypeNodeReference.MODULE);
      } 
      return new TypeNodeReference(functionNames, propertyNames, typeName, realPath, TypeNodeReference.CLASS);
   }
   
   private static class CompileAction implements FileAction<Map<String, TypeNode>> {
   
      private final TypeNodeFinder finder;
      private final Workspace workspace;
      
      public CompileAction(Workspace workspace) {
         this.finder = new TypeNodeFinder(workspace);
         this.workspace = workspace;
      }
      
      @Override
      public Map<String, TypeNode> execute(String reference, File file) throws Exception {
         Project project = workspace.getProject(reference);
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
         
         if(workspace.getLogger().isTrace()) {
            workspace.getLogger().trace("Compiling " + resourcePath + " in project " + reference);
         }
         return finder.parse(name, resourcePath, source);
      }
   }
}