package org.snapscript.studio.search;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.snapscript.studio.core.Workspace;
import org.snapscript.studio.resource.project.Project;
import org.snapscript.studio.resource.project.ProjectLayout;

public class TypeNodeFinder {

   private final ResourceTypeLoader compiler;
   private final Workspace workspace;
   
   public TypeNodeFinder(Workspace workspace) {
      this.compiler = new ResourceTypeLoader(workspace);
      this.workspace = workspace;
   }
   
   public Map<String, TypeNode> parse(String projectName, String resource, String source) {
      Project project = workspace.createProject(projectName);
      ProjectLayout layout = project.getLayout();
      File rootPath = project.getSourcePath();
      
      resource = layout.getDownloadPath(rootPath, resource);
      return parse(project, resource, source);
   }
   
   private Map<String, TypeNode> parse(Project project, String resource, String source) {
      Map<String, TypeNode> types = new HashMap<String, TypeNode>();
      
      try {
         String projectName = project.getProjectName();
         Map<String, TypeNode> nodes = compiler.compileSource(projectName, resource, source);
         Set<String> names = nodes.keySet();
         
         for(String name : names) {
            if(!name.contains(".")) {
               TypeNode node = nodes.get(name);
               String path = node.getResource();
               
               if(path.equals(resource)) {
                  types.put(name, node);
               }
            }
         }
      }catch(Exception cause) {
         return Collections.emptyMap();
      }
      return types;
   }
}