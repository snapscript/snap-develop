package org.snapscript.studio.complete;

import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.studio.Workspace;
import org.snapscript.studio.common.PatternEscaper;
import org.snapscript.studio.common.resource.Resource;
import org.snapscript.studio.index.IndexDatabase;
import org.snapscript.studio.index.IndexNode;
import org.snapscript.studio.index.IndexType;
import org.snapscript.studio.resource.project.Project;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class IndexNodeResource implements Resource {

   private static final String STAR_PATTERN = "_STAR_PATTERN_";
   private static final String EXPRESSION = "expression";

   private final Workspace workspace;
   private final Gson gson;
   
   public IndexNodeResource(Workspace workspace) {
      this.gson = new GsonBuilder().setPrettyPrinting().create();
      this.workspace = workspace;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      String expression = parse(request);
      PrintStream out = response.getPrintStream();
      Path path = request.getPath();
      Project project = workspace.createProject(path);
      Thread thread = Thread.currentThread();
      ClassLoader classLoader = project.getClassLoader();
      thread.setContextClassLoader(classLoader);
      Map<String, IndexNodeData> allNodes = getNodes(project, expression);
      String text = gson.toJson(allNodes);
      response.setContentType("application/json");
      out.println(text);
      out.close();
   }
   
   private String parse(Request request) {      
      String expression = request.getParameter(EXPRESSION);
      
      if(expression != null && !expression.isEmpty()) {
         expression = expression.replace("*", STAR_PATTERN);
         expression = PatternEscaper.escape(expression);
         expression = expression.replace(STAR_PATTERN, ".*");
         return expression + ".*";
      }
      return ".*";
   }
   
   private Map<String, IndexNodeData> getNodes(Project project, String expression) throws Exception {
      IndexDatabase database = project.getIndexDatabase();
      Map<String, IndexNode> nodes = database.getTypeNodesMatching(expression);
      
      return getIndexNodes(project, nodes, expression);
   }
   
   private Map<String, IndexNodeData> getIndexNodes(Project project, Map<String, IndexNode> nodes, String expression) {
      Set<Entry<String, IndexNode>> entries = nodes.entrySet();
      
      if(!entries.isEmpty()) {
         Map<String, IndexNodeData> data = new LinkedHashMap<String, IndexNodeData>();
         
         for(Entry<String, IndexNode> entry : entries) {
            try {
               IndexNode node = entry.getValue();
               IndexType type = node.getType();
               String name = node.getName();
               String fullName = node.getFullName();

               if(type == IndexType.IMPORT) {
                  IndexNode imported = project.getIndexDatabase().getTypeNode(fullName);
                  
                  if(imported != null) {
                     name = imported.getName();
                     type = imported.getType();
                  } else {
                     type = IndexType.CLASS; // hack job
                  }
               }
               String category = type.getName();
               String path = node.getResource();
               String absolute = node.getAbsolutePath();
               String module = node.getModule();
               
               data.put(name +":" + path, new IndexNodeData(name, module, category, path,  absolute));
            } catch(Throwable e) {
               // ignore
            }
         }
         return data;
      }
      return Collections.emptyMap();
   }
   
   private static class IndexNodeData {

      private final String module;
      private final String name;
      private final String type;
      private final String resource;
      private final String extra;
      
      public IndexNodeData(String name, String module, String type, String resource, String extra) {
         this.resource = resource;
         this.module = module;
         this.name = name;
         this.type = type;
         this.extra = extra;
      }

      public String getName() {
         return name;
      }

      public String getType() {
         return type;
      }

      public String getResource() {
         return resource;
      }

      public String getModule() {
         return module;
      }

      public String getExtra() {
         return extra;
      }
   }

}
