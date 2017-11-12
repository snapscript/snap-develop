package org.snapscript.studio.index;
import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.snapscript.common.thread.ThreadPool;
import org.snapscript.core.Context;
import org.snapscript.studio.common.FileAction;
import org.snapscript.studio.common.FileProcessor;
import org.snapscript.studio.common.FileReader;

public class IndexScanner {

   private final FileProcessor<IndexFile> processor;
   private final FileAction<IndexFile> action;
   private final ThreadPool pool;
   private final String project;
   private final File root;
   
   public IndexScanner(Context context, File root, String project) {
      this.pool = new ThreadPool(6);
      this.action = new CompileAction(context, root);
      this.processor = new FileProcessor<IndexFile>(action, pool);
      this.project = project;
      this.root = root;
   }

   public Map<String, IndexNode> findTypesMatching(String expression) throws Exception {
      String path = root.getCanonicalPath();
      Set<IndexFile> results = processor.process(project, path + "/**.snap"); // build all resources
    
      if(!results.isEmpty()) {
         Map<String, IndexNode> matches = new TreeMap<String, IndexNode>();
         
         for(IndexFile file : results) {
            Map<String, IndexNode> nodes = file.getTypeNodes();
            Set<Entry<String, IndexNode>> entries = nodes.entrySet();
            
            for(Entry<String, IndexNode> entry : entries) {
               IndexNode node = entry.getValue();
               String name = node.getFullName();
               
               if(name != null) {
                  IndexType type = node.getType();
                  
                  if(name.matches(expression) && !type.isImport()) {
                     matches.put(name, node);
                  }
               }
            }
         }
         return matches;
      }
      return Collections.emptyMap();
   }
   
   private static class CompileAction implements FileAction<IndexFile> {
   
      private final Context context;
      private final Indexer indexer;
      private final File root;
      
      public CompileAction(Context context, File root) {
         this.indexer = new Indexer();
         this.context = context;
         this.root = root;
      }
      
      @Override
      public IndexFile execute(String reference, File file) throws Exception {
         String rootPath = root.getCanonicalPath();
         String filePath = file.getCanonicalPath();
         String relativePath = filePath.replace(rootPath, "");
         String resourcePath = relativePath.replace(File.separatorChar, '/');
         
         if(!resourcePath.startsWith("/")) {
            resourcePath = "/" + resourcePath;
         }
         String source = FileReader.readText(file);
         
         return indexer.index(context, resourcePath, source);
      }
   }
}
